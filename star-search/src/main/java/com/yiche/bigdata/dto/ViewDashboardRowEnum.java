package com.yiche.bigdata.dto;

/**
 * Created by yangyuchen on 22/12/2017.
 */
public class ViewDashboardRowEnum {
    private String id;
    private String name;
    private String resId;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ViewDashboardRowEnum{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", resId='" + resId + '\'' +
                '}';
    }
}
