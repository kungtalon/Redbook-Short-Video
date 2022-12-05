package org.talon.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.talon.index.IndexComment;
import org.talon.index.IndexVlog;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentMapperCustom {

    List<IndexComment> queryComments(@Param("paramMap") Map<String, Object> map);
}