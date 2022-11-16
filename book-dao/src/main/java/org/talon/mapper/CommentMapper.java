package org.talon.mapper;

import org.springframework.stereotype.Repository;
import org.talon.my.MyMapper;
import org.talon.pojo.Comment;

@Repository
public interface CommentMapper extends MyMapper<Comment> {
}