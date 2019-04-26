package com.yiche.bigdata.entity.pojo;


import java.util.List;

public class DatasourceItem<T> {
    private String id;
    private String name;
    private String type;
    private List<T> tables;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<T> getTables() {
        return tables;
    }

    public void setTables(List<T> tables) {
        this.tables = tables;
    }
}
