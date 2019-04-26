package com.yiche.bigdata.constants.log;

public enum OpType {

    LOADLOG_AUTH("auth"),

    LOADLOG_DOMAIN("domain"),

    AUTHLOG_USERMANAGE("userManage"),

    SENSITIVELOG_DATA("data");

    private String stringName;

    OpType(String stringName){
        this.stringName = stringName;
    }

    public String getStringName() {
        return stringName;
    }
}
