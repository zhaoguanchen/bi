package com.yiche.bigdata.constants;

public enum ReportType {

    COMMON_REPORT(100, "commonReport"),

    REAL_TIME_REPORT(101, "realtimeReport"),

    DAILY_REPORT(201, "dailyReport"),

    WEEK_REPORT(202, "weekReport"),

    MOUTH_REPORT(203, "mounthReport");

    private final int value;
    private final String name;

    ReportType(int value, String name) {
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
