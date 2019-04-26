package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;

public interface PortalPageService {

    /**
     * 查询当前登录用户信息
     * @return Result<T>.class
     */
    Result getCurrentUserInfo();

    /**
     * 查询当前登录用户可用业务线列表
     * @return Result<T>.class
     */
    Result listUserAvailableBusinessLine();

    /**
     * 查询当前登录用户选择的业务线可用菜单列表
     * @param businessLine
     * @return Result<T>.class
     */
    Result listUserAvailableMenu(String businessLine);

    /**
     * 查询大盘信息
     * @param businessLine
     * @return Result<T>.class
     */
    Result getDashboard(String businessLine);

}
