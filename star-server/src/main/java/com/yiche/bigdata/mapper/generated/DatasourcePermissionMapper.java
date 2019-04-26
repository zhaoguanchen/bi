package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.DatasourcePermission;
import com.yiche.bigdata.entity.generated.DatasourcePermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasourcePermissionMapper {
    long countByExample(DatasourcePermissionExample example);

    int deleteByExample(DatasourcePermissionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DatasourcePermission record);

    int insertSelective(DatasourcePermission record);

    List<DatasourcePermission> selectByExample(DatasourcePermissionExample example);

    DatasourcePermission selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DatasourcePermission record, @Param("example") DatasourcePermissionExample example);

    int updateByExample(@Param("record") DatasourcePermission record, @Param("example") DatasourcePermissionExample example);

    int updateByPrimaryKeySelective(DatasourcePermission record);

    int updateByPrimaryKey(DatasourcePermission record);
}