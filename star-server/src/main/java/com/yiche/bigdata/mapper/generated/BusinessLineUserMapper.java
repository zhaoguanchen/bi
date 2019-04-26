package com.yiche.bigdata.mapper.generated;

import com.yiche.bigdata.entity.generated.BusinessLineUser;
import com.yiche.bigdata.entity.generated.BusinessLineUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessLineUserMapper {
    long countByExample(BusinessLineUserExample example);

    int deleteByExample(BusinessLineUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BusinessLineUser record);

    int insertSelective(BusinessLineUser record);

    List<BusinessLineUser> selectByExample(BusinessLineUserExample example);

    BusinessLineUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BusinessLineUser record, @Param("example") BusinessLineUserExample example);

    int updateByExample(@Param("record") BusinessLineUser record, @Param("example") BusinessLineUserExample example);

    int updateByPrimaryKeySelective(BusinessLineUser record);

    int updateByPrimaryKey(BusinessLineUser record);
}