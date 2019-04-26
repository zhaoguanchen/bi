package com.yiche.bigdata.entity.vo;

import org.hibernate.validator.constraints.NotBlank;

public class DashboardVO {

    @NotBlank
    private String name;

    @NotBlank
    private String pid;

    @NotBlank
    private String businessLine;

    @NotBlank
    private String layoutJson;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getBusinessLine() {
        return businessLine;
    }

    public void setBusinessLine(String businessLine) {
        this.businessLine = businessLine;
    }

    public String getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }

    @Override
    public String toString() {
        return "DashboardVO{" +
                "name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", businessLine='" + businessLine + '\'' +
                ", layoutJson='" + layoutJson + '\'' +
                '}';
    }
}
