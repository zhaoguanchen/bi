package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.RoleResRel;
import com.yiche.bigdata.entity.generated.RoleResRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleResRelMapper {
    long countByExample(RoleResRelExample example);

    int deleteByExample(RoleResRelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RoleResRel record);

    int insertSelective(RoleResRel record);

    List<RoleResRel> selectByExampleWithBLOBs(RoleResRelExample example);

    List<RoleResRel> selectByExample(RoleResRelExample example);

    RoleResRel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RoleResRel record, @Param("example") RoleResRelExample example);

    int updateByExampleWithBLOBs(@Param("record") RoleResRel record, @Param("example") RoleResRelExample example);

    int updateByExample(@Param("record") RoleResRel record, @Param("example") RoleResRelExample example);

    int updateByPrimaryKeySelective(RoleResRel record);

    int updateByPrimaryKeyWithBLOBs(RoleResRel record);

    int updateByPrimaryKey(RoleResRel record);
}