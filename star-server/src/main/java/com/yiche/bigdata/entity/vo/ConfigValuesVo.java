package com.yiche.bigdata.entity.vo;

public class ConfigValuesVo {
    private String column;

    private String aggType;

    public ConfigValuesVo(String column, String aggType) {
        this.column = column;
        this.aggType = aggType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAggType() {
        return aggType;
    }

    public void setAggType(String aggType) {
        this.aggType = aggType;
    }
}
