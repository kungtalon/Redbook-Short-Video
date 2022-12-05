package org.talon.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.github.pagehelper.PageHelper;
import com.mysql.cj.util.LRUCache;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;
import org.talon.datautils.RedisConsts;
import org.talon.datautils.RedisUtils;
import org.talon.enums.YesOrNo;
import org.talon.exception.RedbookException;
import org.talon.index.IndexComment;
import org.talon.index.IndexVlog;
import org.talon.mapper.CommentMapper;
import org.talon.mapper.CommentMapperCustom;
import org.talon.pojo.Comment;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.ICommentService;
import org.talon.utils.pagination.MysqlPageUtils;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.vo.CommentInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.talon.datautils.RedisConsts.REDIS_ME_FOLLOWS_VLOGER;
import static org.talon.datautils.RedisConsts.REDIS_USER_LIKE_COMMENT;

/**
 * Created by Zelong
 * On 2022/12/3
 **/

@Service
public class CommentService implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentMapperCustom commentMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private RedisUtils redis;

    private final LRUCache<String, String> cache = new LRUCache<>(100);

    private final ObjectMapper jsonMapper = new ObjectMapper();

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
        return newCommentInfo;
    }

    @Override
    public Comment getCommentById(String commentId) {
        if (cache.containsKey(commentId)) {
            Comment comment = jsonMapper.convertValue(
                    cache.get(commentId), Comment.class);
            return comment;
        }

        Comment result = commentMapper.selectByPrimaryKey(commentId);
        if (result != null) {
            try {
                cache.put(commentId, jsonMapper.writeValueAsString(result));
            } catch (JsonProcessingException ex) {
                // ignore
            }
        }
        return result;
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

        commentMapper.delete(toDelComment);

        redis.decrement(RedisConsts.REDIS_VLOG_COMMENT_COUNT + vlogId, 1);
    }



    private void setCommentInfo(IndexComment comment,
                                String myUserId,
                                String vlogId) {
        String commentId = comment.getCommentId();
        String countsStr = redis.getOr(
            RedisConsts.REDIS_COMMENT_LIKED_COUNT + commentId, "0");
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
