package com.yiche.bigdata.entity.pojo;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/7/7.
 */
public class MappingRow implements Serializable {
    private static final long serialVersionUID = -3336745216153791163L;
    private String type;
    private String field;
    private Boolean include_in_all;
    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Boolean getInclude_in_all() {
        return include_in_all;
    }

    public void setInclude_in_all(Boolean include_in_all) {
        this.include_in_all = include_in_all;
    }

    public MappingRow(String type, String field, Boolean include_in_all) {
        this.type = type;
        this.field = field;
        this.include_in_all = include_in_all;
    }

    public MappingRow(String type, String field) {
        this.type = type;
        this.field = field;
    }

    public MappingRow() {
    }

    @Override
    public String toString() {
        return "MappingRow{" +
                "type='" + type + '\'' +
                ", field='" + field + '\'' +
                ", include_in_all=" + include_in_all +
                ", format='" + format + '\'' +
                '}';
    }
}
