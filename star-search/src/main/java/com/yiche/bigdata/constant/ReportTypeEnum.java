package com.yiche.bigdata.constant;

import com.alibaba.druid.util.StringUtils;

/**
 * Created by yangyuchen on 24/01/2018.
 */
public enum ReportTypeEnum {
    DAILY_REPORT("dailyReport"),
    WEEK_REPORT("weekReport"),
    MONTH_REPORT("monthReport"),
    COMMON_REPORT("commonReport"),
    REAL_TIME_REPORT("realtimeReport"),
    UNSET("unset");

    private String strName;

    ReportTypeEnum(String strName) {
        this.strName = strName;
    }

    public String getStrName() {
        return strName;
    }

    public static ReportTypeEnum getType(String reportTypeString) {
        if (StringUtils.equalsIgnoreCase(reportTypeString, DAILY_REPORT.getStrName())) {
            return DAILY_REPORT;
        } else if (StringUtils.equalsIgnoreCase((reportTypeString), WEEK_REPORT.getStrName())) {
            return WEEK_REPORT;
        } else if (StringUtils.equalsIgnoreCase(reportTypeString, MONTH_REPORT.getStrName())) {
            return MONTH_REPORT;
        } else if (StringUtils.equalsIgnoreCase(reportTypeString, COMMON_REPORT.getStrName())) {
            return COMMON_REPORT;
        } else {
            return UNSET;
        }
    }

    @Override
    public String toString() {
        return "ReportTypeEnum{" +
                "strName='" + strName + '\'' +
                '}';
    }
}
