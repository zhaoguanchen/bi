package com.yiche.bigdata.entity.vo;

import java.util.List;

public class DataSetColumnsVo {
    private String resId;
    private String name;
    private List<TableColumnInfoVo> columns;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TableColumnInfoVo> getColumns() {
        return columns;
    }

    public void setColumns(List<TableColumnInfoVo> columns) {
        this.columns = columns;
    }
}
