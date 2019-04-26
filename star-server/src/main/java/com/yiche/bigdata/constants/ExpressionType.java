package com.yiche.bigdata.constants;

public enum ExpressionType {

    AggregateFunction("0", "聚合函数"),

    Arithmetic("1", "四则运算");

    private final String value;
    private final String name;

    ExpressionType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

}
