package org.talon.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.talon.index.IndexVlog;

import java.util.List;
import java.util.Map;

@Repository
public interface VlogMapperCustom {

    List<IndexVlog> getIndexVlogList(@Param("paramMap") Map<String, Object> map);

    List<IndexVlog> getVlogDetailById(@Param("paramMap") Map<String, Object> map);

    List<IndexVlog> getMyLikedVlogList(@Param("paramMap") Map<String, Object> map);

    List<IndexVlog> getMyFollowVlogList(@Param("paramMap") Map<String, Object> map);

    List<IndexVlog> queryVlogByCommentId(@Param("paramMap") Map<String, Object> map);
}