package com.yiche.bigdata.entity.vo;

import java.util.List;

public class ConfigRowsVo {
    private String columnName;
    private String fileterType;
    private List<String> value;
    private String id;


    public ConfigRowsVo(String columnName, String fileterType, List<String> value, String id) {
        this.columnName = columnName;
        this.fileterType = fileterType;
        this.value = value;
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFileterType() {
        return fileterType;
    }

    public void setFileterType(String fileterType) {
        this.fileterType = fileterType;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
