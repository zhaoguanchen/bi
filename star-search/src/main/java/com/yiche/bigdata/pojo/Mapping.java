package com.yiche.bigdata.pojo;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/7/7.
 */
public class Mapping implements Serializable {
    private static final long serialVersionUID = -2564891041624743587L;
    private MappingEsObject es;
    private MappingRow row;

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

    public Mapping(){}

    public Mapping(MappingEsObject es, MappingRow row) {
        this.es = es;
        this.row = row;
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "es=" + es +
                ", row=" + row +
                '}';
    }
}
