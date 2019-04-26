package com.yiche.bigdata.constants;

public enum DataSourceType {

    ES_TYPE(100, "Elasticsearch"),

    JDBC_TYPE(101, "jdbc"),

    KYLIN_TYPE(102, "kylin");

    private final int value;
    private final String name;

    DataSourceType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static DataSourceType getByValue(int value) {
        for(DataSourceType typeEnum : DataSourceType.values()) {
            if(typeEnum.value == value) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + value);
    }


    public static String dataSourceLevelPermissionTypes() {
        return "101";
    }

}
