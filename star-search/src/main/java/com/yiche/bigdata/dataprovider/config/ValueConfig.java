package com.yiche.bigdata.dataprovider.config;

import com.yiche.bigdata.dto.req.ValueReq;

import java.io.Serializable;

/**
 * Created by zyong on 2017/1/9.
 */
public class ValueConfig implements Serializable{
    private String column;
    private String aggType;

    public static ValueConfig builtFrom(ValueReq valueReq){
        ValueConfig valueConfig = new ValueConfig();
        valueConfig.setAggType(valueReq.getAggType());
        valueConfig.setColumn(valueReq.getColumn());
        return valueConfig;
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

