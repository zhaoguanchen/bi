package com.yiche.bigdata.entity.vo;

import java.util.List;
import java.util.Map;

public class ReportPermissionVo {
    private String resId;

    private String name;

    private Map<String, String> permissions;

    private String layoutJson;

    private List<ComponentPermissionVo> widgetsPermission;

    public Map<String, String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, String> permissions) {
        this.permissions = permissions;
    }

    public List<ComponentPermissionVo> getWidgetsPermission() {
        return widgetsPermission;
    }

    public void setWidgetsPermission(List<ComponentPermissionVo> widgetsPermission) {
        this.widgetsPermission = widgetsPermission;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }
}
