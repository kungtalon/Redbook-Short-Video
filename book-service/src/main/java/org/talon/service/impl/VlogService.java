package org.talon.service.impl;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talon.enums.YesOrNo;
import org.talon.mapper.VlogMapper;
import org.talon.mapper.VlogMapperCustom;
import org.talon.pojo.Vlog;
import org.talon.service.IVlogService;
import org.talon.utils.pagination.MysqlPageUtils;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.vo.IndexVlog;
import org.talon.vo.VlogInfo;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/11/13
 **/

@Service
public class VlogService implements IVlogService {
    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private VlogMapperCustom vlogMapperCustom;

    @Autowired
    private Sid sid;

    @Override
    public void createVlog(VlogInfo vi) {
        String vid = sid.nextShort();

        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vi, vlog);

        vlog.setId(vid);

        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNo.NO.type);

        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());

        vlogMapper.insert(vlog);
    }

    @Override
    public PagedGridResult getIndexVlogList(String search, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }
        List<IndexVlog> results = vlogMapperCustom.getIndexVlogList(map);

        return MysqlPageUtils.setterPagedGrid(results, page);
    }

    public IndexVlog getVlogDetail(String vlogId) {
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);

        List<IndexVlog> list = vlogMapperCustom.getVlogDetailById(map);

        if (list != null && !list.isEmpty()){
            IndexVlog vlog = list.get(0);
            return vlog;
        }
        return null;
    }

    @Override
    public boolean changeVisibility(String userId, String vlogId, Integer setPrivate) {

        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", vlogId);
        criteria.andEqualTo("vlogerId", userId);
        criteria.andEqualTo("isPrivate",  1 - setPrivate);

        Vlog toUpdateVlog = new Vlog();
        toUpdateVlog.setIsPrivate(setPrivate);

        return vlogMapper.updateByExampleSelective(toUpdateVlog, example) > 0;
    }

    @Override
    public PagedGridResult queryMyVlogList(String userId,
                                           Integer page,
                                           Integer pageSize,
                                           Integer isPrivate) {
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId", userId);
        criteria.andEqualTo("isPrivate",  isPrivate);

        PageHelper.startPage(page, pageSize);
        List<Vlog> list = vlogMapper.selectByExample(example);

        return MysqlPageUtils.setterPagedGrid(list, page);
    }
}
