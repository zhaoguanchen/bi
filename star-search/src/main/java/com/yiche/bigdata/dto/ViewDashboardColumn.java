package com.yiche.bigdata.dto;


import java.util.List;


/**
 * Created by yangyuchen on 22/12/2017.
 */
public class ViewDashboardColumn {
    private String columnName;
    private String columnKey;
    private List<ViewDashboardRowEnum> valueList;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public List<ViewDashboardRowEnum> getValueList() {
        return valueList;
    }

    public void setValueList(List<ViewDashboardRowEnum> valueList) {
        this.valueList = valueList;
    }

    @Override
    public String toString() {
        return "ViewDashboardColumn{" +
                "columnName='" + columnName + '\'' +
                ", columnKey='" + columnKey + '\'' +
                ", valueList=" + valueList +
                '}';
    }
}
