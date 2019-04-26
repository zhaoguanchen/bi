package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.generated.ResTreeExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResTreeMapper {
    long countByExample(ResTreeExample example);

    int deleteByExample(ResTreeExample example);

    int deleteByPrimaryKey(String id);

    int insert(ResTree record);

    int insertSelective(ResTree record);

    List<ResTree> selectByExample(ResTreeExample example);

    ResTree selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ResTree record, @Param("example") ResTreeExample example);

    int updateByExample(@Param("record") ResTree record, @Param("example") ResTreeExample example);

    int updateByPrimaryKeySelective(ResTree record);

    int updateByPrimaryKey(ResTree record);

    Integer getResCountByName(@Param("resName") String resName, @Param("type") Integer type, @Param("businessLine") String businessLine);

}