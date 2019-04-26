package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.Dashboard;
import com.yiche.bigdata.entity.generated.DashboardExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardMapper {
    long countByExample(DashboardExample example);

    int deleteByExample(DashboardExample example);

    int deleteByPrimaryKey(String resId);

    int insert(Dashboard record);

    int insertSelective(Dashboard record);

    List<Dashboard> selectByExampleWithBLOBs(DashboardExample example);

    List<Dashboard> selectByExample(DashboardExample example);

    Dashboard selectByPrimaryKey(String resId);

    int updateByExampleSelective(@Param("record") Dashboard record, @Param("example") DashboardExample example);

    int updateByExampleWithBLOBs(@Param("record") Dashboard record, @Param("example") DashboardExample example);

    int updateByExample(@Param("record") Dashboard record, @Param("example") DashboardExample example);

    int updateByPrimaryKeySelective(Dashboard record);

    int updateByPrimaryKeyWithBLOBs(Dashboard record);

    int updateByPrimaryKey(Dashboard record);
}