package com.yiche.bigdata.dto.req;

/**
 * Created by yangyuchen on 25/01/2018.
 */
public class DataTypeReq {
    private String type;//string, double, float, integer, date, etc.
    private String format;//e.g. date's "yyyy-mm-dd", "yyyy-mm"

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "DataTypeReq{" +
                "type='" + type + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
