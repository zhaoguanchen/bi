package com.yiche.bigdata.constants.log;

public enum InfoType {

    LOADLOG("loadlog"),

    AUTHLOG("authlog"),

    SENSITIVELOG("sensitivelog");

    private String stringName;

    InfoType(String stringName){
        this.stringName = stringName;
    }

    public String getStringName() {
        return stringName;
    }
}
