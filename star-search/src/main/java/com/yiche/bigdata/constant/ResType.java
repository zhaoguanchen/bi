package com.yiche.bigdata.constant;

/**
 * Created by yangyuchen on 23/12/2017.
 */
public enum ResType {
    ROW_ENUM("rowenum");

    private String stringName;

    ResType(String stringName){
        this.stringName = stringName;
    }

    public String getStringName() {
        return stringName;
    }

    @Override
    public String toString() {
        return "ResType{" +
                "stringName='" + stringName + '\'' +
                '}';
    }
}
