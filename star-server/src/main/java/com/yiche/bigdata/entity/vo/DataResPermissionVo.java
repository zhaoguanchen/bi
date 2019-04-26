package com.yiche.bigdata.entity.vo;

import java.util.List;

public class DataResPermissionVo {

    private List<PermissionVo> optPermission;

    private List<TreeNodePermissionVo> dataPermission;

    public List<PermissionVo> getOptPermission() {
        return optPermission;
    }

    public void setOptPermission(List<PermissionVo> optPermission) {
        this.optPermission = optPermission;
    }

    public List<TreeNodePermissionVo> getDataPermission() {
        return dataPermission;
    }

    public void setDataPermission(List<TreeNodePermissionVo> dataPermission) {
        this.dataPermission = dataPermission;
    }
}
