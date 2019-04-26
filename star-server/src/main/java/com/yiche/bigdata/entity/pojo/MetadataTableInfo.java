package com.yiche.bigdata.entity.pojo;


/**
 * Created by jmy on 2017/7/17.
 */
public class MetadataTableInfo implements Comparable<MetadataTableInfo> {
    private String resId;
    private String table;
    private String srcUrl;
    private String index;
    private String name;
    private String desc;
    private String database;
    private String frequency;
    private int bizType;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public int getBizType() {
        return bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public String toString() {
        return "MetadataTableInfo{" +
                "table='" + table + '\'' +
                ", srcUrl='" + srcUrl + '\'' +
                ", index='" + index + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", database='" + database + '\'' +
                ", frequency='" + frequency + '\'' +
                ", bizType=" + bizType +
                ", database='" + database + '\'' +
                '}';
    }


    public int compareTo(MetadataTableInfo arg0) {
        return this.getTable().compareTo(arg0.getTable());
    }
}
