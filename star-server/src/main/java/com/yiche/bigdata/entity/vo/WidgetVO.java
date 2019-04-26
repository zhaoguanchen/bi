package com.yiche.bigdata.entity.vo;

import org.hibernate.validator.constraints.NotBlank;

public class WidgetVO {

    @NotBlank
    private String name;

    private String pid;

    @NotBlank
    private String businessLine;

    @NotBlank
    private String dataJson;

    @NotBlank
    private String datasetId;

    @NotBlank
    private String chartType;

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

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    @Override
    public String toString() {
        return "WidgetVO{" +
                "name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", businessLine='" + businessLine + '\'' +
                ", dataJson='" + dataJson + '\'' +
                ", datasetId='" + datasetId + '\'' +
                ", chartType='" + chartType + '\'' +
                '}';
    }
}
