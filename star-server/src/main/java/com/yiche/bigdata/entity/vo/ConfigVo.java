package com.yiche.bigdata.entity.vo;

import java.util.List;

public class ConfigVo {

    private List<ConfigRowsVo> rows;
    private List<String>  columns;
    private List<String>  filters;
    private List<ConfigValuesVo>  values;


    public ConfigVo(List<ConfigRowsVo> rows, List<String> columns, List<String> filters, List<ConfigValuesVo> values) {
        this.rows = rows;
        this.columns = columns;
        this.filters = filters;
        this.values = values;
    }

    public List<ConfigRowsVo> getRows() {
        return rows;
    }

    public void setRows(List<ConfigRowsVo> rows) {
        this.rows = rows;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    public List<ConfigValuesVo> getValues() {
        return values;
    }

    public void setValues(List<ConfigValuesVo> values) {
        this.values = values;
    }
}
