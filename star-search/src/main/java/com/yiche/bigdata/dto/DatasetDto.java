package com.yiche.bigdata.dto;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by root on 2018/6/7.
 */
public class DatasetDto {
    private Long datasourceId;
    private String datasourceType;
    private Map<String, String> datasourceConfig;
    private Map<String, String> query;
    private Long interval;
    private JSONObject schema;


    public DatasetDto() {
    }

    public DatasetDto(Long datasourceId, Map<String, String> query, Long interval, JSONObject schema) {
        this.datasourceId = datasourceId;
        this.query = query;
        this.interval = interval;
        this.schema = schema;
    }

    public DatasetDto(Long datasourceId, String datasourceType, Map<String, String> datasourceConfig, Map<String, String> query, Long interval, JSONObject schema) {
        this.datasourceId = datasourceId;
        this.datasourceType = datasourceType;
        this.datasourceConfig = datasourceConfig;
        this.query = query;
        this.interval = interval;
        this.schema = schema;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public Map<String, String> getDatasourceConfig() {
        return datasourceConfig;
    }

    public void setDatasourceConfig(Map<String, String> datasourceConfig) {
        this.datasourceConfig = datasourceConfig;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public JSONObject getSchema() {
        return schema;
    }

    public void setSchema(JSONObject schema) {
        this.schema = schema;
    }
}
