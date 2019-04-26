package com.yiche.bigdata.entity.vo;

import java.util.List;

public class ExpressionValidateVO {


    private String dataSourceId;

    private List<PairData> columnList;

    private String query;

    private String expression;

    private String type;


    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public List<PairData> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<PairData> columnList) {
        this.columnList = columnList;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
