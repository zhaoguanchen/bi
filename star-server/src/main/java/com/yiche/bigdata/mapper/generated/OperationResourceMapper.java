package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.OperationResource;
import com.yiche.bigdata.entity.generated.OperationResourceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationResourceMapper {
    long countByExample(OperationResourceExample example);

    int deleteByExample(OperationResourceExample example);

    int deleteByPrimaryKey(String id);

    int insert(OperationResource record);

    int insertSelective(OperationResource record);

    List<OperationResource> selectByExample(OperationResourceExample example);

    OperationResource selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") OperationResource record, @Param("example") OperationResourceExample example);

    int updateByExample(@Param("record") OperationResource record, @Param("example") OperationResourceExample example);

    int updateByPrimaryKeySelective(OperationResource record);

    int updateByPrimaryKey(OperationResource record);
}