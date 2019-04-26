package com.yiche.bigdata.pojo;

import com.yiche.bigdata.dataprovider.config.AggConfig;
import com.yiche.bigdata.dto.DatasetDto;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by root on 2018/6/6.
 */
public class QueryRequestBody implements Serializable{
    private Long datasourceId;
    private Map<String, String> query;
    private DatasetDto DatasetDto;
    private AggConfig aggConfig;
    private Boolean reload;
    private String columnName;
    private Map<String, String> dataSource;
    private String type;

    public QueryRequestBody(Long datasourceId, Map<String, String> query, DatasetDto DatasetDto, AggConfig aggConfig, Boolean reload, String columnName, Map<String, String> dataSource, String type) {
        this.datasourceId = datasourceId;
        this.query = query;
        this.DatasetDto = DatasetDto;
        this.aggConfig = aggConfig;
        this.reload = reload;
        this.columnName = columnName;
        this.dataSource = dataSource;
        this.type = type;
    }

    public QueryRequestBody() {
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

    public DatasetDto getDatasetDto() {
        return DatasetDto;
    }

    public void setDatasetDto(DatasetDto DatasetDto) {
        this.DatasetDto = DatasetDto;
    }

    public AggConfig getAggConfig() {
        return aggConfig;
    }

    public void setAggConfig(AggConfig aggConfig) {
        this.aggConfig = aggConfig;
    }

    public Boolean getReload() {
        return reload;
    }

    public void setReload(Boolean reload) {
        this.reload = reload;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Map<String, String> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "QueryRequestBody{" +
                "datasourceId=" + datasourceId +
                ", query=" + query +
                ", DatasetDto=" + DatasetDto +
                ", aggConfig=" + aggConfig +
                ", reload=" + reload +
                ", columnName='" + columnName + '\'' +
                ", dataSource=" + dataSource +
                ", type='" + type + '\'' +
                '}';
    }
}
