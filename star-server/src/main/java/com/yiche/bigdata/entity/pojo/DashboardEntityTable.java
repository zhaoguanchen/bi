package com.yiche.bigdata.entity.pojo;

public class DashboardEntityTable {

    private Long id;
    private String name;
    private String chName;
    private String dbName;
    private String permission;
    private int bizType;
    private String index;

    public int getBizType() {
        return bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    @Override
    public String toString() {
        return "DashboardEntityTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", chName='" + chName + '\'' +
                ", dbName='" + dbName + '\'' +
                ", permission='" + permission + '\'' +
                ", bizType=" + bizType +
                ", index='" + index + '\'' +
                '}';
    }
}