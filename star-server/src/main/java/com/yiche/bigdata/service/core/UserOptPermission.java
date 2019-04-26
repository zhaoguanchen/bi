package com.yiche.bigdata.service.core;

import java.util.HashMap;
import java.util.Map;

public class UserOptPermission {
    private String businessLine;
    private BaseTree OptTree;
    private Map<String, String> optPermissions = new HashMap<>();

    public String getBusinessLine() {
        return businessLine;
    }

    public void setBusinessLine(String businessLine) {
        this.businessLine = businessLine;
    }

    public BaseTree getOptTree() {
        return OptTree;
    }

    public void setOptTree(BaseTree optTree) {
        OptTree = optTree;
    }

    public Map<String, String> getOptPermissions() {
        return optPermissions;
    }

    public void setOptPermissions(Map<String, String> optPermissions) {
        this.optPermissions = optPermissions;
    }
}
