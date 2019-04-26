package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.FilterComponent;
import com.yiche.bigdata.entity.generated.FilterComponentExample;
import com.yiche.bigdata.mapper.generated.FilterComponentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterComponentDao {

    @Autowired
    private FilterComponentMapper filterComponentMapper;

    public List<FilterComponent> findByRoleAndResId(String roleId, String resId) {
        FilterComponentExample example = new FilterComponentExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResIdEqualTo(resId);
        return filterComponentMapper.selectByExample(example);
    }

    public boolean deleteByRoleAndResId(String roleId, String resId) {
        FilterComponentExample example = new FilterComponentExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResIdEqualTo(resId);
        return filterComponentMapper.deleteByExample(example) > 0;
    }

    public boolean save(FilterComponent rowPermission) {
        return filterComponentMapper.insertSelective(rowPermission) > 0;
    }

    public List<FilterComponent> findByRoles(List<String> roleIdList) {
        FilterComponentExample example = new FilterComponentExample();
        example.createCriteria().andRoleIdIn(roleIdList);
        return filterComponentMapper.selectByExample(example);
    }

}
