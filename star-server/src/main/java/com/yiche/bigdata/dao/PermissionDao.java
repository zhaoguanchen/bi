package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.Permission;
import com.yiche.bigdata.entity.generated.PermissionExample;
import com.yiche.bigdata.mapper.generated.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionDao {

    @Autowired
    private PermissionMapper permissionMapper;

    public List<Permission> listAll() {
        PermissionExample example = new PermissionExample();
        example.createCriteria().andStateEqualTo(0);
        return permissionMapper.selectByExample(example);
    }
}
