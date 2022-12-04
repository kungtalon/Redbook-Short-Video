package org.talon.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.PageObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.talon.datautils.RedisUtils;
import org.talon.enums.YesOrNo;
import org.talon.mapper.FansMapper;
import org.talon.mapper.FansMapperCustom;
import org.talon.pojo.Fans;
import org.talon.pojo.Vlog;
import org.talon.service.IFansService;
import org.talon.utils.pagination.MysqlPageUtils;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.vo.FansInfo;
import org.talon.vo.VloggerInfo;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.talon.datautils.RedisConsts.*;

/**
 * Created by Zelong
 * On 2022/11/15
 **/
@Slf4j
@Service
public class FansService implements IFansService {

    @Autowired
    private FansMapper fansMapper;

    @Autowired
    private FansMapperCustom fansMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private RedisUtils redis;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Transactional
    @Override
    public boolean doFollow(String myUserId, String followUserId) {
        String fid = sid.nextShort();

        Fans fans = new Fans();
        fans.setId(fid);
        fans.setFanId(myUserId);
        fans.setVlogerId(followUserId);

        // check whether followUserId has followed me
        Fans vlogerIsMyFans = queryFansRelation(followUserId, myUserId);
        if (vlogerIsMyFans != null) {
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            vlogerIsMyFans.setIsFanFriendOfMine(YesOrNo.YES.type);
            fansMapper.updateByPrimaryKey(vlogerIsMyFans);
        }
        // else, the fans relation is not friends

        redis.set(REDIS_ME_FOLLOWS_VLOGER + myUserId + ":" + followUserId, "1");

        // counting with redis
        redis.increment(REDIS_MY_FOLLOWING_COUNT + myUserId, 1);
        redis.increment(REDIS_MY_FANS_COUNT + followUserId, 1);

        fansMapper.insert(fans);
        return true;
    }

    @Transactional
    @Override
    public boolean doCancel(String myUserId, String followUserId) {
        // check relationship
        boolean meIsVlogersFans = redis.exists(
                REDIS_ME_FOLLOWS_VLOGER + myUserId + ":" + followUserId);
        if (!meIsVlogersFans) {
            return false;
        }

        Fans meFollowVloger = queryFansRelation(myUserId, followUserId);
        if (meFollowVloger != null && meFollowVloger.getIsFanFriendOfMine() == YesOrNo.YES.type) {
            Fans vlogerIsMyFans = queryFansRelation(followUserId, myUserId);
            vlogerIsMyFans.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateByPrimaryKey(vlogerIsMyFans);
        }

        // delete from database and redis
        fansMapper.delete(meFollowVloger);
        redis.decrement(REDIS_MY_FOLLOWING_COUNT + myUserId, 1);
        redis.decrement(REDIS_MY_FANS_COUNT + followUserId, 1);
        redis.del(REDIS_ME_FOLLOWS_VLOGER + myUserId + ":" + followUserId);
        return true;
    }

    public Fans queryFansRelation(String fansId, String vlogerId) {
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId", vlogerId);
        criteria.andEqualTo("fanId",  fansId);

        List<Fans> resultList = fansMapper.selectByExample(example);

        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public PagedGridResult queryMyFollows(String myUserId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myUserId);

        PageHelper.startPage(page, pageSize);
        List<VloggerInfo> resultList = fansMapperCustom.queryMyFollows(map);

        return MysqlPageUtils.setterPagedGrid(resultList, page);
    }

    @Override
    public PagedGridResult queryMyFans(String myUserId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myUserId);

        PageHelper.startPage(page, pageSize);
        List<FansInfo> resultList = fansMapperCustom.queryMyFans(map);

        return MysqlPageUtils.setterPagedGrid(resultList, page);
    }
}
