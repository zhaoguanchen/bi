package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.generated.DatasetExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetMapper {
    long countByExample(DatasetExample example);

    int deleteByExample(DatasetExample example);

    int deleteByPrimaryKey(String resId);

    int insert(Dataset record);

    int insertSelective(Dataset record);

    List<Dataset> selectByExampleWithBLOBs(DatasetExample example);

    List<Dataset> selectByExample(DatasetExample example);

    Dataset selectByPrimaryKey(String resId);

    int updateByExampleSelective(@Param("record") Dataset record, @Param("example") DatasetExample example);

    int updateByExampleWithBLOBs(@Param("record") Dataset record, @Param("example") DatasetExample example);

    int updateByExample(@Param("record") Dataset record, @Param("example") DatasetExample example);

    int updateByPrimaryKeySelective(Dataset record);

    int updateByPrimaryKeyWithBLOBs(Dataset record);

    int updateByPrimaryKey(Dataset record);
}