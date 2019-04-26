package com.yiche.bigdata.pojo;

/**
 * Created by jmy on 2017/7/18.
 */
public class NodeMappingInfo {
    private String key;
    private String name;
    private MappingEsObject es;
    private MappingRow row;

    private boolean enumerable;
    private String dataDictTable;
    private int dimenType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDimenType() {
        return dimenType;
    }

    public void setDimenType(int dimenType) {
        this.dimenType = dimenType;
    }

    public boolean isEnumerable() {
        return enumerable;
    }

    public void setEnumerable(boolean enumerable) {
        this.enumerable = enumerable;
    }

    public String getDataDictTable() {
        return dataDictTable;
    }

    public void setDataDictTable(String dataDictTable) {
        this.dataDictTable = dataDictTable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MappingEsObject getEs() {
        return es;
    }

    public void setEs(MappingEsObject es) {
        this.es = es;
    }

    public MappingRow getRow() {
        return row;
    }

    public void setRow(MappingRow row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "NodeMappingInfo{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", es=" + es +
                ", row=" + row +
                ", enumerable=" + enumerable +
                ", dataDictTable='" + dataDictTable + '\'' +
                ", dimenType=" + dimenType +
                '}';
    }
}
