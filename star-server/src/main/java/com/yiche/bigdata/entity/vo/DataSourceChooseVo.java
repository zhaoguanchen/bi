package com.yiche.bigdata.entity.vo;

import java.util.List;

public class DataSourceChooseVo<E> {
    private String id;

    private String name;

    private String type;

    private List<E> tables;

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

    public List<E> getTables() {
        return tables;
    }

    public void setTables(List<E> tables) {
        this.tables = tables;
    }
}
