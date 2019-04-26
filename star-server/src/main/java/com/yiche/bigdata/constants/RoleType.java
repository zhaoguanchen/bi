package com.yiche.bigdata.constants;

public enum RoleType {

    SUPER_ADMIN_TYPE(100, "超级管理员"),

    BUSINESS_ADMIN_TYPE(201, "业务线管理员"),

    BUSINESS_TYPE(202, "业务线普通角色");

    private final int value;
    private final String name;

    RoleType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

}
