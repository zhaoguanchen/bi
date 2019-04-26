package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.generated.WidgetExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetMapper {
    long countByExample(WidgetExample example);

    int deleteByExample(WidgetExample example);

    int deleteByPrimaryKey(String resId);

    int insert(Widget record);

    int insertSelective(Widget record);

    List<Widget> selectByExampleWithBLOBs(WidgetExample example);

    List<Widget> selectByExample(WidgetExample example);

    Widget selectByPrimaryKey(String resId);

    int updateByExampleSelective(@Param("record") Widget record, @Param("example") WidgetExample example);

    int updateByExampleWithBLOBs(@Param("record") Widget record, @Param("example") WidgetExample example);

    int updateByExample(@Param("record") Widget record, @Param("example") WidgetExample example);

    int updateByPrimaryKeySelective(Widget record);

    int updateByPrimaryKeyWithBLOBs(Widget record);

    int updateByPrimaryKey(Widget record);

    int countByDateSetId(String dataSetId);
}