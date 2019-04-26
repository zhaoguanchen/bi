package com.yiche.bigdata.entity.pojo;


import java.util.List;

/**
 * Created by jmy on 2017/7/18.
 */

/**
 * 表详情描述，包括表的内部字段
 */
public class TableDetail {
    private String table;
    private String index;
    private String srcUrl;
    private List<NodeMappingInfo> dimenList;
    private List<NodeMappingInfo> metricList;
    private String name;
    private String desc;
    private String database;
    private String frequency;

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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public List<NodeMappingInfo> getDimenList() {
        return dimenList;
    }

    public void setDimenList(List<NodeMappingInfo> dimenList) {
        this.dimenList = dimenList;
    }

    public List<NodeMappingInfo> getMetricList() {
        return metricList;
    }

    public void setMetricList(List<NodeMappingInfo> metricList) {
        this.metricList = metricList;
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
        return "TableDetail{" +
                "table='" + table + '\'' +
                ", index='" + index + '\'' +
                ", srcUrl='" + srcUrl + '\'' +
                ", dimenList=" + dimenList +
                ", metricList=" + metricList +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", database='" + database + '\'' +
                ", frequency='" + frequency + '\'' +
                '}';
    }
}
