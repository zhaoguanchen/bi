package com.yiche.bigdata.entity.pojo;

import java.util.List;

public class RowPermissionItem {
    private String roleId;
    private String resId;
    private List<RowPermissionOptionItem> permissions;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public List<RowPermissionOptionItem> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RowPermissionOptionItem> permissions) {
        this.permissions = permissions;
    }
}
