package com.yiche.bigdata.entity.dto.search;

/**
 * Created by yangyuchen on 24/01/2018.
 */
public class ValueReq {
    private String column;
    private String aggType;

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

    @Override
    public String toString() {
        return "ValueReq{" +
                "column='" + column + '\'' +
                ", aggType='" + aggType + '\'' +
                '}';
    }
}
