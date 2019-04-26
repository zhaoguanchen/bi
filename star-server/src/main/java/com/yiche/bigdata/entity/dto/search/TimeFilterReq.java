package com.yiche.bigdata.entity.dto.search;

import java.util.List;

/**
 * Created by yangyuchen on 24/01/2018.
 */
public class TimeFilterReq {
    private String timePrimaryColumn;
    private String type; // daily 日报  weekly 周报  monthly 月报  general 普通报表
    private List<String> values;

    public String getTimePrimaryColumn() {
        return timePrimaryColumn;
    }

    public void setTimePrimaryColumn(String timePrimaryColumn) {
        this.timePrimaryColumn = timePrimaryColumn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "TimeFilterReq{" +
                "timePrimaryColumn='" + timePrimaryColumn + '\'' +
                ", type='" + type + '\'' +
                ", values=" + values +
                '}';
    }
}
