package com.yiche.bigdata.entity.pojo;


import java.util.List;

public class BusinessLineItem<T> {
    private String id;
    private String name;
    private String description;
    private List<DatasourceItem<T>> datasource;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DatasourceItem<T>> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DatasourceItem<T>> datasource) {
        this.datasource = datasource;
    }
}
