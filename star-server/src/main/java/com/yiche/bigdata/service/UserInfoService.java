package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.DomainUserInfo;

import java.util.List;

public interface UserInfoService {

    /**
     * 通过token获取当前用户名
     * @param token
     * @return String.class
     */
    String getUserNameByToken(String token);

    /**
     * 根据域账号查询用户信息
     * @param username
     * @return DomainUserInfo.class
     */
    DomainUserInfo getUserInfoByUserName(String username);

    /**
     * 模糊查询域账号
     * @param username
     * @return List<DomainUserInfo>.class
     */
    List<DomainUserInfo> searchDomainUser(String username);

}
