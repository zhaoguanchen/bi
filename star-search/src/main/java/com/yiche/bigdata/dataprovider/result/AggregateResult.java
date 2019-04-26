package com.yiche.bigdata.dataprovider.result;

import com.yiche.bigdata.dataprovider.config.AggConfig;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yfyuan on 2017/1/18.
 */
public class AggregateResult implements Serializable{
    private List<ColumnIndex> columnList;
    private String[][] data;
    private String kpi_type;
    private RatioResult linkRatioResult;
    private RatioResult yoyRatioResult;
    private AggConfig aggConfig;

    public AggregateResult(List<ColumnIndex> columnList, String[][] data) {
        this.columnList = columnList;
        this.data = data;
    }
    public AggregateResult(List<ColumnIndex> columnList, String[][] data, String kpi_type) {
        this.columnList = columnList;
        this.data = data;
        this.kpi_type = kpi_type;
    }
    public AggregateResult() {
    }
    public AggConfig getAggConfig() {
        return aggConfig;
    }

    public void setAggConfig(AggConfig aggConfig) {
        this.aggConfig = aggConfig;
    }

    public List<ColumnIndex> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ColumnIndex> columnList) {
        this.columnList = columnList;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public String getKpi_type() {
        return kpi_type;
    }

    public void setKpi_type(String kpi_type) {
        this.kpi_type = kpi_type;
    }

    public RatioResult getLinkRatioResult() {
        return linkRatioResult;
    }

    public void setLinkRatioResult(RatioResult linkRatioResult) {
        this.linkRatioResult = linkRatioResult;
    }

    public RatioResult getYoyRatioResult() {
        return yoyRatioResult;
    }

    public void setYoyRatioResult(RatioResult yoyRatioResult) {
        this.yoyRatioResult = yoyRatioResult;
    }
}
