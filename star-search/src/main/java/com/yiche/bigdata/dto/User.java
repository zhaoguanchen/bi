package com.yiche.bigdata.dto;

import java.io.Serializable;

/**
 * Created by yfyuan on 2016/9/29.
 */
public class User implements Serializable {

    private String userId;
    private String company;
    private String department;
    private String name;
    private String chnName;

    public String getChnName() {
        return chnName;
    }

    public void setChnName(String chnName) {
        this.chnName = chnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", company='" + company + '\'' +
                ", department='" + department + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

