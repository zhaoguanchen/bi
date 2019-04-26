package com.yiche.bigdata.entity.pojo;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/7/7.
 */
public class MappingEsObject implements Serializable {
    private static final long serialVersionUID = 7655051900234071116L;
    private String type;
    private String format;
    private Boolean include_in_all;

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

    public Boolean getInclude_in_all() {
        return include_in_all;
    }

    public void setInclude_in_all(Boolean include_in_all) {
        this.include_in_all = include_in_all;
    }

    public MappingEsObject(String type, String format, Boolean include_in_all) {
        this.type = type;
        this.format = format;
        this.include_in_all = include_in_all;
    }

    public MappingEsObject(String type, String format) {
        this.type = type;
        this.format = format;
    }

    public MappingEsObject(String type) {
        this.type = type;
    }

    public MappingEsObject() {

    }

    @Override
    public String toString() {
        return "MappingEsObject{" +
                "type='" + type + '\'' +
                ", format='" + format + '\'' +
                ", include_in_all=" + include_in_all +
                '}';
    }
}
