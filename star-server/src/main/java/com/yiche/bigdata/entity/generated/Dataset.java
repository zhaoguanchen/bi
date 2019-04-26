package com.yiche.bigdata.entity.generated;

public class Dataset {
    private String resId;

    private String name;

    private String timePrimaryKey;

    private String dataJson;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId == null ? null : resId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTimePrimaryKey() {
        return timePrimaryKey;
    }

    public void setTimePrimaryKey(String timePrimaryKey) {
        this.timePrimaryKey = timePrimaryKey == null ? null : timePrimaryKey.trim();
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson == null ? null : dataJson.trim();
    }
}