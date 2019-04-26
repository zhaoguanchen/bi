package com.yiche.bigdata.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessLineMapper {

    List<String> getAllDepartment(@Param("businessLine") String businessLine);
}
