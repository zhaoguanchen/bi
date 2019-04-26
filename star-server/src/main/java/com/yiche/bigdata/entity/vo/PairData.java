package com.yiche.bigdata.entity.vo;

/**
 * @Author:zhaoguanchen
 * @Date:2019/4/2
 * @Description:
 */
public class PairData {
    private String key;
    private String value;

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

    public PairData() {
    }

    public PairData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "PairData{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
