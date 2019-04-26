package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.FilterComponent;
import com.yiche.bigdata.entity.generated.FilterComponentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterComponentMapper {
    long countByExample(FilterComponentExample example);

    int deleteByExample(FilterComponentExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(FilterComponent record);

    int insertSelective(FilterComponent record);

    List<FilterComponent> selectByExample(FilterComponentExample example);

    FilterComponent selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") FilterComponent record, @Param("example") FilterComponentExample example);

    int updateByExample(@Param("record") FilterComponent record, @Param("example") FilterComponentExample example);

    int updateByPrimaryKeySelective(FilterComponent record);

    int updateByPrimaryKey(FilterComponent record);
}