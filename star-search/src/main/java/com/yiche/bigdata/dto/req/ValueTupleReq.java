package com.yiche.bigdata.dto.req;

/**
 * Created by yangyuchen on 24/01/2018.
 */
public class ValueTupleReq {
    private String key;
    private String value;

    public ValueTupleReq() {
    }

    public ValueTupleReq(String str) {
        this.key = str;
        this.value = str;
    }

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

    @Override
    public String toString() {
        return "{" +
                "\"key\":\"" + key + '\"' +
                ", \"value\":\"" + value + '\"' +
                '}';
    }
}
