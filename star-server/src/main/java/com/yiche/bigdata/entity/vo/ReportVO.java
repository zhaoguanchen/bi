package com.yiche.bigdata.entity.vo;

import org.hibernate.validator.constraints.NotBlank;

public class ReportVO {

    @NotBlank
    private String name;

    @NotBlank
    private String pid;

    @NotBlank
    private String businessLine;

    @NotBlank
    private String datasetId;

    @NotBlank
    private String layoutJson;

    @NotBlank
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ReportVO{" +
                "name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", businessLine='" + businessLine + '\'' +
                ", datasetId='" + datasetId + '\'' +
                ", layoutJson='" + layoutJson + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
