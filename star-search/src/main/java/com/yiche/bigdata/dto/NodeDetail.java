package com.yiche.bigdata.dto;


/**
 * Created by jmy on 2017/7/19.
 */
public class NodeDetail {
    private String table;
    private String key;
    private String name;
    private String alias;
    private String nodeType;
    private String dataDictTable;
    private int enumerable;
    private boolean isVirtual;
    private String regular;
    private Mapping mapping;
    private String desc;
    private int dimenType;



    public int getDimenType() {
        return dimenType;
    }

    public void setDimenType(int dimenType) {
        this.dimenType = dimenType;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getDataDictTable() {
        return dataDictTable;
    }

    public void setDataDictTable(String dataDictTable) {
        this.dataDictTable = dataDictTable;
    }

    public int getEnumerable() {
        return enumerable;
    }

    public void setEnumerable(int enumerable) {
        this.enumerable = enumerable;
    }

    public boolean getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "NodeDetail{" +
                "table='" + table + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", dataDictTable='" + dataDictTable + '\'' +
                ", enumerable=" + enumerable +
                ", isVirtual=" + isVirtual +
                ", regular='" + regular + '\'' +
                ", mapping=" + mapping +
                ", desc='" + desc + '\'' +
                ", dimenType=" + dimenType +
                '}';
    }
}
