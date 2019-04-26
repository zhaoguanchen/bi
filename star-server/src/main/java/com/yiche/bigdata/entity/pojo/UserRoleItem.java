package com.yiche.bigdata.entity.pojo;

import com.yiche.bigdata.entity.vo.UserRoleVo;

import java.util.List;

/**
 * @Author:zhaoguanchen
 * @Date:2019/2/27
 * @Description:
 */
public class UserRoleItem {

    private String userName;
    private String businessLine;
    private List<UserRoleVo> arr;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBusinessLine() {
        return businessLine;
    }

    public void setBusinessLine(String businessLine) {
        this.businessLine = businessLine;
    }

    public List<UserRoleVo> getArr() {
        return arr;
    }

    public void setArr(List<UserRoleVo> arr) {
        this.arr = arr;
    }
}
