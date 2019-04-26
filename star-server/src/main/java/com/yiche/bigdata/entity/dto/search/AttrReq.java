package com.yiche.bigdata.entity.dto.search;

import java.util.List;

/**
 * Created by yangyuchen on 24/01/2018.
 */
public class AttrReq {
    private String columnName;
    private String filterType;
    private List<ValueTupleReq> values;
    private String id;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public List<ValueTupleReq> getValues() {
        return values;
    }

    public void setValues(List<ValueTupleReq> values) {
        this.values = values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
