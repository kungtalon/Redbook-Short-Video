package org.talon.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.talon.my.MyMapper;
import org.talon.pojo.Fans;
import org.talon.vo.FansInfo;
import org.talon.vo.VloggerInfo;

import java.util.List;
import java.util.Map;

@Repository
public interface FansMapperCustom extends MyMapper<Fans> {
    List<VloggerInfo> queryMyFollows(@Param("paramMap") Map<String, Object> map);

    List<FansInfo> queryMyFans(@Param("paramMap") Map<String, Object> map);
}