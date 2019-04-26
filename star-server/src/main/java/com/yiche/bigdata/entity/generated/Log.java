package com.yiche.bigdata.entity.generated;

import java.util.Date;

public class Log {
    private Long id;

    private String userId;

    private String ip;

    private Date logTime;

    private String infoType;

    private String opType;

    private String actDesc;

    private String optional;

    private String department;

    private String act;

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
        this.userId = userId == null ? null : userId.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
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
        this.infoType = infoType == null ? null : infoType.trim();
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType == null ? null : opType.trim();
    }

    public String getActDesc() {
        return actDesc;
    }

    public void setActDesc(String actDesc) {
        this.actDesc = actDesc == null ? null : actDesc.trim();
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional == null ? null : optional.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act == null ? null : act.trim();
    }


    public Log() {
    }

    public Log(Long id, String userId, String ip, Date logTime, String infoType, String opType, String act, String actDesc, String optional, String department) {
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
}