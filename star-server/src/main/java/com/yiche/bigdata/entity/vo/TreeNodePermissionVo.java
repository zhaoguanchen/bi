package com.yiche.bigdata.entity.vo;

import java.util.List;

public class TreeNodePermissionVo {

    private String id;

    private String name;

    private String pid;

    private Integer type;

    private Integer sort;

    private boolean recursion;

    private List<TreeNodePermissionVo> children;

    private List<PermissionVo> permissions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public boolean isRecursion() {
        return recursion;
    }

    public void setRecursion(boolean recursion) {
        this.recursion = recursion;
    }

    public List<PermissionVo> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionVo> permissions) {
        this.permissions = permissions;
    }

    public List<TreeNodePermissionVo> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNodePermissionVo> children) {
        this.children = children;
    }
}
