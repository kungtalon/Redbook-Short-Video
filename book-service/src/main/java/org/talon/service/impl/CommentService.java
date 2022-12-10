package org.talon.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talon.cache.CacheManager;
import org.talon.datautils.RedisConsts;
import org.talon.datautils.RedisUtils;
import org.talon.enums.MessageEnum;
import org.talon.enums.YesOrNo;
import org.talon.exception.RedbookException;
import org.talon.index.IndexComment;
import org.talon.mapper.CommentMapper;
import org.talon.mapper.CommentMapperCustom;
import org.talon.mapper.VlogMapperCustom;
import org.talon.pojo.Comment;
import org.talon.pojo.Vlog;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.ICommentService;
import org.talon.service.IMsgService;
import org.talon.service.IVlogService;
import org.talon.utils.pagination.MysqlPageUtils;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.vo.CommentInfo;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.talon.datautils.RedisConsts.*;

/**
 * Created by Zelong
 * On 2022/12/3
 **/

@Slf4j
@Service
public class CommentService implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentMapperCustom commentMapperCustom;

    @Autowired
    private VlogMapperCustom vlogMapperCustom;

    @Autowired
    private IVlogService vlogService;

    @Autowired
    private IMsgService msgService;

    @Autowired
    private Sid sid;

    @Autowired
    private RedisUtils redis;

    private final Cache<String, Object> cache =
            CacheManager.ofTable(CacheManager.TableNames.COMMENT_CACHE);

    @Override
    public CommentInfo createComment(CommentInfo commentInfo) {
        String commentId = sid.nextShort();
        Comment comment = new Comment();

        BeanUtils.copyProperties(commentInfo, comment);

        comment.setId(commentId);
        comment.setLikeCounts(0);
        comment.setCreateTime(new Date());

        int success = commentMapper.insert(comment);
        if (success == 0) {
            throw new RedbookException(ResponseStatusEnum.COMMENT_CREATE_FAILD);
        }

        redis.increment(RedisConsts.REDIS_VLOG_COMMENT_COUNT
                + commentInfo.getVlogId(), 1);

        CommentInfo newCommentInfo = new CommentInfo();
        BeanUtils.copyProperties(comment, newCommentInfo);

        // system message : comment / reply
        Map<String, Object> msgContent = new HashMap<>();
        msgContent.put("commentId", commentId);
        msgContent.put("commentContent", comment.getContent());

        Vlog vlog = vlogService.getVlogById(comment.getVlogId());
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());

        Integer type = MessageEnum.COMMENT_VLOG.type;
        if (StringUtils.isNotBlank(comment.getFatherCommentId())
            && !"0".equalsIgnoreCase(comment.getFatherCommentId())) {
            type = MessageEnum.REPLY_YOU.type;
            msgContent.put("fatherCommentId", comment.getFatherCommentId());
        }

        msgService.createMsg(comment.getCommentUserId(),
                comment.getVlogerId(),
                type,
                msgContent);

        return newCommentInfo;
    }

    @Override
    public Comment getCommentById(String commentId) {
        try {
            return (Comment) cache.get(commentId, () -> {
                Comment result = commentMapper.selectByPrimaryKey(commentId);
                cache.put(commentId, result);
                return result;
            });
        } catch (ExecutionException ex) {
            log.error("Error when loading value from comment cache");
        }

        return null;
    }

    @Override
    public void like(String commentId, String userId) {
        redis.increment(REDIS_COMMENT_LIKED_COUNT + commentId, 1);
        redis.setHashValue(REDIS_USER_LIKE_COMMENT + commentId, userId, "1");

        // system message: like comment
        Comment comment = getCommentById(commentId);
        Vlog vlog = vlogService.getVlogById(comment.getVlogId());

        Map<String, Object> msgContent = new HashMap();
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());
        msgContent.put("commentId", commentId);

        msgService.createMsg(userId,
                comment.getCommentUserId(),
                MessageEnum.LIKE_COMMENT.type,
                msgContent);
    }

    @Override
    public void unlinke(String commentId, String userId) {
        redis.decrement(REDIS_COMMENT_LIKED_COUNT + commentId, 1);
        redis.hdel(REDIS_USER_LIKE_COMMENT + commentId, userId);

        msgService.deleteAllMessages(userId,
                "",
                MessageEnum.LIKE_COMMENT.type,
                Collections.singletonMap("commentId", commentId));
    }

    @Override
    public PagedGridResult queryVlogComments(String vlogId,
                                             String userId,
                                             Integer page,
                                             Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);

        List<IndexComment> list = commentMapperCustom.queryComments(map);

        list.forEach(com -> setCommentInfo(com, userId, vlogId));

        return MysqlPageUtils.setterPagedGrid(list, page);
    }

    @Override
    public void deleteComment(String commentUserId,
                              String commentId,
                              String vlogId) {
        Comment toDelComment = new Comment();
        toDelComment.setCommentUserId(commentUserId);
        toDelComment.setId(commentId);

        cache.invalidate(commentId);
        commentMapper.delete(toDelComment);
        // also, delete all replies associated with this comment
        // never mind...
//        Example example = new Example(Comment.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("fatherCommentId", commentId);
//        commentMapper.deleteByExample(example);

        redis.decrement(RedisConsts.REDIS_VLOG_COMMENT_COUNT + vlogId, 1);

        msgService.deleteAllMessagesByCommentId(commentId);
    }


    private void setCommentInfo(IndexComment comment,
                                String myUserId,
                                String vlogId) {
        String commentId = comment.getCommentId();
        String countsStr = redis.getOr(
            REDIS_COMMENT_LIKED_COUNT + commentId, "0");
        comment.setLikeCounts(Integer.valueOf(countsStr));

        // check whether the user liked the comment
        if (!StringUtils.isBlank(myUserId)) {
            String doILikeComment = redis.hget(REDIS_USER_LIKE_COMMENT + commentId, myUserId);
            if (StringUtils.isNotBlank(doILikeComment) && "1".equalsIgnoreCase(doILikeComment)) {
                comment.setIsLike(YesOrNo.YES.type);
            }
        }
    }

}
