package com.yiche.bigdata.dto;

/**
 * Created by jmy on 2017/10/10.
 */
public class PairData {
    private String key;
    private String value;
    private String column;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public PairData(){}

    public PairData(String key, String value, String column) {
        this.key = key;
        this.value = value;
        this.column = column;
    }


    public PairData(String key, String value) {
        this.key = key;
        this.value = value;
        this.column = column;
    }
    @Override
    public String toString() {
        return "PairData{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", column='" + column + '\'' +
                '}';
    }
}
