package com.yiche.bigdata.entity.dto.search;

import java.util.List;

/**
 * Created by yangyuchen on 24/01/2018.
 */
public class CfgReq {
    private List<AttrReq> rows;
    private List<AttrReq> columns;
    private List<AttrReq> filters;
    private List<ValueReq> values;

    public List<AttrReq> getRows() {
        return rows;
    }

    public void setRows(List<AttrReq> rows) {
        this.rows = rows;
    }

    public List<AttrReq> getColumns() {
        return columns;
    }

    public void setColumns(List<AttrReq> columns) {
        this.columns = columns;
    }

    public List<AttrReq> getFilters() {
        return filters;
    }

    public void setFilters(List<AttrReq> filters) {
        this.filters = filters;
    }

    public List<ValueReq> getValues() {
        return values;
    }

    public void setValues(List<ValueReq> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "CfgReq{" +
                "rows=" + rows +
                ", columns=" + columns +
                ", filters=" + filters +
                ", values=" + values +
                '}';
    }
}
