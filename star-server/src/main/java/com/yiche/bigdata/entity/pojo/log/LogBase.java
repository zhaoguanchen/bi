package com.yiche.bigdata.entity.pojo.log;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Created by lb on 2018/4/25.
 * 基本信息
 */
public class LogBase {
    //static
    private static final String USERTYPE_MANUAL = "manual";
    private static final String USERTYPE_MASHINE = "machine";

    //private
    private String userId;
    private String userType;
    private String hostname;
    private String client;
    private String ip;
    private Long timestamp;
    private String originalLogId;
    private String originalCMD;

    //构造
    public LogBase(String userId, String ipAddr) {
        this.userId = userId;
        this.userType = USERTYPE_MANUAL;
        this.hostname = StringUtils.EMPTY;
        this.client = StringUtils.EMPTY;
        this.ip = ipAddr;
        this.timestamp = new Date().getTime();
        this.originalLogId = StringUtils.EMPTY;
        this.originalCMD = StringUtils.EMPTY;
    }
    //tostring


    //get set
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOriginalLogId() {
        return originalLogId;
    }

    public void setOriginalLogId(String originalLogId) {
        this.originalLogId = originalLogId;
    }

    public String getOriginalCMD() {
        return originalCMD;
    }

    public void setOriginalCMD(String originalCMD) {
        this.originalCMD = originalCMD;
    }
}
