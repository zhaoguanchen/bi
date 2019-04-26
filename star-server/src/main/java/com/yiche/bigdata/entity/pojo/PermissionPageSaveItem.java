package com.yiche.bigdata.entity.pojo;

import com.yiche.bigdata.entity.vo.TreeNodePermissionVo;

import java.util.List;

public class PermissionPageSaveItem {
    private String resType;
    private String roleId;
    private List<TreeNodePermissionVo> resourcePermissionList;

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public List<TreeNodePermissionVo> getResourcePermissionList() {
        return resourcePermissionList;
    }

    public void setResourcePermissionList(List<TreeNodePermissionVo> resourcePermissionList) {
        this.resourcePermissionList = resourcePermissionList;
    }
}
