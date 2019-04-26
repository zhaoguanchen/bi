package com.yiche.bigdata.entity.vo;

import org.hibernate.validator.constraints.NotBlank;

public class DirectoryVO {


    private String resId;
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String pid;

    @NotBlank
    private String businessLine;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "DirectoryVO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pid='" + pid + '\'' +
                ", businessLine='" + businessLine + '\'' +
                '}';
    }
}
