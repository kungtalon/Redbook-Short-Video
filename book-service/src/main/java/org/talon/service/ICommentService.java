package org.talon.service;

import org.springframework.stereotype.Service;
import org.talon.pojo.Comment;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.vo.CommentInfo;

/**
 * Created by Zelong
 * On 2022/12/3
 **/

@Service
public interface ICommentService {
    CommentInfo createComment(CommentInfo commentInfo);

    Comment getCommentById(String commentId);

    void like(String commentId, String userId);

    void unlinke(String commentId, String userId);

    PagedGridResult queryVlogComments(String vlogId,
                                      String userId,
                                      Integer page,
                                      Integer pageSize);

    void deleteComment(String commentUserId,
                       String commentId,
                       String vlogId);
}
