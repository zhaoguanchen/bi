package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.RoleResRel;
import com.yiche.bigdata.entity.generated.RoleResRelExample;
import com.yiche.bigdata.mapper.generated.RoleResRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class RoleResRelDao {

    @Autowired
    private RoleResRelMapper roleResRelMapper;

    public List<RoleResRel> listRoleResource(String roleId, Integer type) {
        RoleResRelExample example = new RoleResRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResTypeEqualTo(type);
        return roleResRelMapper.selectByExampleWithBLOBs(example);
    }

    public List<RoleResRel> listRoleResource(String roleId, List<Integer> type) {
        RoleResRelExample example = new RoleResRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResTypeIn(type);
        return roleResRelMapper.selectByExampleWithBLOBs(example);
    }

    public List<RoleResRel> listRoleResource(String roleId) {
        RoleResRelExample example = new RoleResRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        return roleResRelMapper.selectByExampleWithBLOBs(example);
    }

    public RoleResRel getRoleRes(String roleId, String resId) {
        RoleResRelExample example = new RoleResRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResIdEqualTo(resId);
        List<RoleResRel> searchResult = roleResRelMapper.selectByExampleWithBLOBs(example);
        if (CollectionUtils.isEmpty(searchResult)) {
            return null;
        }
        return searchResult.get(0);
    }

    public boolean updateRoleResPermissions(String roleId, String resId, String permissions) {
        RoleResRel roleResRel = new RoleResRel();
        roleResRel.setPermissions(permissions);
        RoleResRelExample example = new RoleResRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResIdEqualTo(resId);
        return roleResRelMapper.updateByExampleSelective(roleResRel, example) > 0;
    }

    public boolean deleteRoleRes(String roleId, String resId) {
        RoleResRelExample example = new RoleResRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResIdEqualTo(resId);
        return roleResRelMapper.deleteByExample(example) > 0;
    }

    public boolean deleteRoleResType(String roleId, Integer resType) {
        RoleResRelExample example = new RoleResRelExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andResTypeEqualTo(resType);
        return roleResRelMapper.deleteByExample(example) > 0;
    }


    public boolean insert(RoleResRel roleResRel) {
        return roleResRelMapper.insertSelective(roleResRel) > 0;
    }

}
