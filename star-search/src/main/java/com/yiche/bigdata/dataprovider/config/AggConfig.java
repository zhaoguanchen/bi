package com.yiche.bigdata.dataprovider.config;

import com.yiche.bigdata.pojo.FilterPartExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyong on 2017/1/9.
 */
public class AggConfig {

//    private List<DimensionConfig> rows;
//    private List<DimensionConfig> columns;
//    private List<ConfigComponent> filters;
//    private List<ValueConfig> values;
//
//    public List<DimensionConfig> getRows() {
//        return rows;
//    }
//
//    public void setRows(List<DimensionConfig> rows) {
//        this.rows = rows;
//    }
//
//    public List<DimensionConfig> getColumns() {
//        return columns;
//    }
//
//    public void setColumns(List<DimensionConfig> columns) {
//        this.columns = columns;
//    }
//
//    public List<ConfigComponent> getFilters() {
//        return filters;
//    }
//
//    public void setFilters(List<ConfigComponent> filters) {
//        this.filters = filters;
//    }
//
//    public List<ValueConfig> getValues() {
//        return values;
//    }
//
//    public void setValues(List<ValueConfig> values) {
//        this.values = values;
//    }
private List<DimensionConfig> rows = new ArrayList<>();
    private List<DimensionConfig> columns = new ArrayList<>();
    private List<ConfigComponent> filters = new ArrayList<>();
    private List<ValueConfig> values = new ArrayList<>();
    List<FilterPartExpression> filterPartExpressionList = new ArrayList<>();
    private boolean hasMonthRelativeTime;

    public List<DimensionConfig> getRows() {
        return rows;
    }

    public void setRows(List<DimensionConfig> rows) {
        this.rows = rows;
    }

    public List<DimensionConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<DimensionConfig> columns) {
        this.columns = columns;
    }

    public List<ConfigComponent> getFilters() {
        return filters;
    }

    public void setFilters(List<ConfigComponent> filters) {
        this.filters = filters;
    }

    public List<ValueConfig> getValues() {
        return values;
    }

    public void setValues(List<ValueConfig> values) {
        this.values = values;
    }

    public boolean isHasMonthRelativeTime() {
        return hasMonthRelativeTime;
    }

    public void setHasMonthRelativeTime(boolean hasMonthRelativeTime) {
        this.hasMonthRelativeTime = hasMonthRelativeTime;
    }

    public List<FilterPartExpression> getFilterPartExpressionList() {
        return filterPartExpressionList;
    }

    public void setFilterPartExpressionList(List<FilterPartExpression> filterPartExpressionList) {
        this.filterPartExpressionList = filterPartExpressionList;
    }
}
