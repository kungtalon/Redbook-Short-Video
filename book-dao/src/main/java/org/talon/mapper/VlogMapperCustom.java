package org.talon.mapper;

import net.sf.jsqlparser.statement.create.table.Index;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.talon.my.MyMapper;
import org.talon.pojo.Vlog;
import org.talon.vo.IndexVlog;

import java.util.List;
import java.util.Map;

@Repository
public interface VlogMapperCustom {

    List<IndexVlog> getIndexVlogList(@Param("paramMap") Map<String, Object> map);

    List<IndexVlog> getVlogDetailById(@Param("paramMap") Map<String, Object> map);

    List<IndexVlog> getMyLikedVlogList(@Param("paramMap") Map<String, Object> map);

    List<IndexVlog> getMyFollowVlogList(@Param("paramMap") Map<String, Object> map);
}