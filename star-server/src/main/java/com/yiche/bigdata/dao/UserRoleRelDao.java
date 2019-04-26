package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.UserRoleRel;
import com.yiche.bigdata.entity.generated.UserRoleRelExample;
import com.yiche.bigdata.mapper.generated.UserRoleRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRoleRelDao {

    @Autowired
    private UserRoleRelMapper userRoleRelMapper;

    public boolean add(String userId, String roleId) {
        UserRoleRel userRoleRel = new UserRoleRel();
        userRoleRel.setUserId(userId);
        userRoleRel.setRoleId(roleId);
        return userRoleRelMapper.insert(userRoleRel) > 0;
    }

    public List<UserRoleRel> findUsersByRoleId(String roleId) {
        UserRoleRelExample example = new UserRoleRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        return userRoleRelMapper.selectByExample(example);
    }

    public List<UserRoleRel> listUserRoles(String userId) {
        UserRoleRelExample example = new UserRoleRelExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return userRoleRelMapper.selectByExample(example);
    }


    public boolean delete(String userId, String roleId) {
        UserRoleRelExample example = new UserRoleRelExample();
        example.createCriteria().andUserIdEqualTo(userId).andRoleIdEqualTo(roleId);
        return userRoleRelMapper.deleteByExample(example) > 0;
    }

    public boolean delete(String userId, List<String> roleIdList) {
        UserRoleRelExample example = new UserRoleRelExample();
        example.createCriteria().andUserIdEqualTo(userId).andRoleIdIn(roleIdList);
        return userRoleRelMapper.deleteByExample(example) > 0;
    }

}
