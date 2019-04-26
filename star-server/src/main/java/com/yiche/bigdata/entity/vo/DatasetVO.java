package com.yiche.bigdata.entity.vo;

import org.hibernate.validator.constraints.NotBlank;

public class DatasetVO {

    @NotBlank
    private String name;

    @NotBlank
    private String timePrimaryKey;

    @NotBlank
    private String dataJson;

    private String pid;

    @NotBlank
    private String businessLine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTimePrimaryKey() {
        return timePrimaryKey;
    }

    public void setTimePrimaryKey(String timePrimaryKey) {
        this.timePrimaryKey = timePrimaryKey == null ? null : timePrimaryKey.trim();
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson == null ? null : dataJson.trim();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getBusinessLine() {
        return businessLine;
    }

    public void setBusinessLine(String businessLine) {
        this.businessLine = businessLine;
    }

    @Override
    public String toString() {
        return "DatasetVO{" +
                ", name='" + name + '\'' +
                ", timePrimaryKey='" + timePrimaryKey + '\'' +
                ", dataJson='" + dataJson + '\'' +
                ", pid='" + pid + '\'' +
                ", businessLine='" + businessLine + '\'' +
                '}';
    }
}
