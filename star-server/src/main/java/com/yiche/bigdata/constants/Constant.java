package com.yiche.bigdata.constants;

/**
 * @Author:zhaoguanchen
 * @Date:2019/4/17
 * @Description:
 */
public enum Constant {
    DEFAULT_DIRECTORY("default");






    public String getStringName() {
        return stringName;
    }

    Constant(String stringName) {
        this.stringName = stringName;
    }

    private String stringName;

}
