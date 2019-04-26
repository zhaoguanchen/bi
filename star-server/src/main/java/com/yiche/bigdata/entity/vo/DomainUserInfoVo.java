package com.yiche.bigdata.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.generated.BusinessLineUser;
import org.apache.commons.lang.StringUtils;

/**
 * Created by jmy on 2017/12/13.
 */
public class DomainUserInfoVo {
    private String name;
    private String userId;
    private String department;
    private String position;
    @JsonIgnore
    private String seat;
    private String realName;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String mobile;

    public DomainUserInfoVo() {
    }

    public DomainUserInfoVo(DomainUserInfo info) {
        this.name = info.getUserName();
        this.position = info.getPostTitle();
        this.userId = info.getDomainAccount();
        this.seat = info.getSeat();
        this.realName = info.getRealName();
        this.email = info.getEmail();
        this.mobile = info.getMobile();
        this.department = changeDepartmentFormat(info.getDepartment());
    }

    public DomainUserInfoVo(BusinessLineUser user) {
        this.name = user.getUserName();
        this.realName = user.getRealName();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.department = changeDepartmentFormat(user.getDepartment());
    }

    private String changeDepartmentFormat(String department) {
        if (StringUtils.isNotEmpty(department)) {
            return department.substring(5).replaceAll("-", "/");
        } else {
            return "null";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
