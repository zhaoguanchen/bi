package com.yiche.bigdata.constants;

public enum ConnectorType {

    AND(0, "AND"),

    OR(1, "OR");

    private final int value;
    private final String name;

    ConnectorType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

}
