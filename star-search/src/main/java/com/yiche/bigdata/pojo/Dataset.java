package com.yiche.bigdata.pojo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import java.util.Map;

public class Dataset {
    private String datasourceId;
    private Map<String, String> query;
    private Long interval;
    private JSONObject schema;

    public Dataset(DashboardDataset dataset) {
        JSONObject data = JSONObject.parseObject(dataset.getData());
        this.query = Maps.transformValues(data.getJSONObject("query"), Functions.toStringFunction());
        this.datasourceId = data.getString("datasource");
        this.interval = data.getLong("interval");
        this.schema = data.getJSONObject("schema");
    }

    public JSONObject getSchema() {
        return schema;
    }

    public void setSchema(JSONObject schema) {
        this.schema = schema;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
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
}
