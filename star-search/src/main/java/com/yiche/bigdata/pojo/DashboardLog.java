package com.yiche.bigdata.pojo;

import java.util.Date;

/**
 * Created by root on 2018/4/20.
 */
public class DashboardLog {
    private Long id;
    private String userId;
    private String ip;
    private Date logTime;
    private String infoType;
    private String opType;
    private String act;
    private String actDesc;
    private String optional;
    private String department;
    public DashboardLog() {
    }

    public DashboardLog(Long id, String userId, String ip, Date logTime, String infoType, String opType, String act, String actDesc, String optional, String department) {
        this.id = id;
        this.userId = userId;
        this.ip = ip;
        this.logTime = logTime;
        this.infoType = infoType;
        this.opType = opType;
        this.act = act;
        this.actDesc = actDesc;
        this.optional = optional;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getActDesc() {
        return actDesc;
    }

    public void setActDesc(String actDesc) {
        this.actDesc = actDesc;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "DashboardLog{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", ip='" + ip + '\'' +
                ", logTime=" + logTime +
                ", infoType='" + infoType + '\'' +
                ", opType='" + opType + '\'' +
                ", act='" + act + '\'' +
                ", actDesc='" + actDesc + '\'' +
                ", optional='" + optional + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
