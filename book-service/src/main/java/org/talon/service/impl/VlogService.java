package org.talon.service.impl;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.talon.datautils.RedisConsts;
import org.talon.datautils.RedisUtils;
import org.talon.enums.YesOrNo;
import org.talon.mapper.MyLikedVlogMapper;
import org.talon.mapper.VlogMapper;
import org.talon.mapper.VlogMapperCustom;
import org.talon.pojo.MyLikedVlog;
import org.talon.pojo.Vlog;
import org.talon.service.IVlogService;
import org.talon.utils.pagination.MysqlPageUtils;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.index.IndexVlog;
import org.talon.vo.VlogInfo;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.talon.datautils.RedisConsts.REDIS_ME_FOLLOWS_VLOGER;

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
    private MyLikedVlogMapper likedVlogMapper;

    @Autowired
    private FansService fansService;

    @Autowired
    private RedisUtils redis;

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
    public Vlog vlogExistsById(String vlogId) {
        return vlogMapper.selectByPrimaryKey(vlogId);
    }

    @Override
    public PagedGridResult getIndexVlogList(String userId,
                                            String search,
                                            Integer page,
                                            Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }
        List<IndexVlog> results = vlogMapperCustom.getIndexVlogList(map);
        results.forEach(vlog -> setVlogInfo(vlog, userId));

        return MysqlPageUtils.setterPagedGrid(results, page);
    }

    public IndexVlog getVlogDetail(String vlogId, String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);

        List<IndexVlog> list = vlogMapperCustom.getVlogDetailById(map);

        list.forEach(vlog -> setVlogInfo(vlog, userId));

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

    @Transactional
    @Override
    public boolean userLikeVlog(String userId, String vlogId, String vloggerId) {
        String rid = sid.nextShort();

        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setId(rid);
        likedVlog.setUserId(userId);
        likedVlog.setVlogId(vlogId);

        boolean success = likedVlogMapper.insert(likedVlog) > 0;

        if (!success) {
            return false;
        }
        // after likes, remember to change counting in redis
        redis.increment(RedisConsts.REDIS_MY_VLOGS_BE_LIKED_COUNT + vloggerId, 1);
        redis.increment(RedisConsts.REDIS_VLOG_BE_LIKED_COUNT + vlogId, 1);

        redis.set(RedisConsts.REDIS_USER_LIKE_VLOG + userId + ":" + vlogId, "1");

        return success;
    }

    @Transactional
    @Override
    public boolean userUndoLikeVlog(String userId, String vlogId, String vloggerId) {
        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setUserId(userId);
        likedVlog.setVlogId(vlogId);

        boolean success = likedVlogMapper.delete(likedVlog) > 0;

        if (!success) {
            return false;
        }

        redis.decrement(RedisConsts.REDIS_MY_VLOGS_BE_LIKED_COUNT + vloggerId, 1);
        redis.decrement(RedisConsts.REDIS_VLOG_BE_LIKED_COUNT + vlogId, 1);

        redis.del(RedisConsts.REDIS_USER_LIKE_VLOG + userId + ":" + vlogId);

        return success;
    }

    @Override
    public PagedGridResult getMyLikedVlogList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<IndexVlog> list = vlogMapperCustom.getMyLikedVlogList(map);

        return MysqlPageUtils.setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult getMyFollowVlogList(String userId,
                                               Integer page,
                                               Integer pageSize,
                                               Boolean onlyFriend) {
        PageHelper.startPage(page, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("myUserId", userId);
        if (onlyFriend){
            map.put("onlyFriend", "1");
        } else {
            map.put("onlyFriend", "0");
        }

        List<IndexVlog> list = vlogMapperCustom.getMyFollowVlogList(map);

        list.forEach(vlog -> setVlogInfo(vlog, userId));

        return MysqlPageUtils.setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public Integer getVlogLikedCounts(String vlogId) {
        String vlogLikedCntStr = redis.getOr(
                RedisConsts.REDIS_VLOG_BE_LIKED_COUNT + vlogId,
                "0");
        return Integer.valueOf(vlogLikedCntStr);
    }

    private boolean doesUserLikedVlog(String userId, String vlogId) {
        String doesLike = redis.get(RedisConsts.REDIS_USER_LIKE_VLOG + userId + ":" + vlogId);
        if (StringUtils.isNotBlank(doesLike) && doesLike.equalsIgnoreCase("1")) {
            return true;
        }
        return false;
    }

    private void setVlogInfo(IndexVlog vlog, String userId) {
        String vid = vlog.getVlogId();
        String vlogerId = vlog.getVlogerId();
        if (StringUtils.isNotBlank(userId)) {
            boolean doIFollowVlogger = redis.exists(
                    REDIS_ME_FOLLOWS_VLOGER + userId + ":" + vlogerId);
            vlog.setDoIFollowVloger(doIFollowVlogger);
            vlog.setDoILikeThisVlog(doesUserLikedVlog(userId, vid));
        }
        vlog.setLikeCounts(getVlogLikedCounts(vid));
    }
}
