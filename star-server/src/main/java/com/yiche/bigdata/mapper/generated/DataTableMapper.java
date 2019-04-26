package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.DataTable;
import com.yiche.bigdata.entity.generated.DataTableExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataTableMapper {
    long countByExample(DataTableExample example);

    int deleteByExample(DataTableExample example);

    int deleteByPrimaryKey(String resId);

    int insert(DataTable record);

    int insertSelective(DataTable record);

    List<DataTable> selectByExample(DataTableExample example);

    DataTable selectByPrimaryKey(String resId);

    int updateByExampleSelective(@Param("record") DataTable record, @Param("example") DataTableExample example);

    int updateByExample(@Param("record") DataTable record, @Param("example") DataTableExample example);

    int updateByPrimaryKeySelective(DataTable record);

    int updateByPrimaryKey(DataTable record);
}