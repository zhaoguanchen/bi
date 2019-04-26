package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;

import java.util.Map;

public interface ReportPageService {

    /**
     * 查询报表添加等权限
     * @param pid
     * @param search
     * @return Result<T>.class
     */
    Result getReportList(String pid, String search);

    /**
     * 查询当前登录报表列表
     * @param queryItem
     * @return Result<T>.class
     */
    Result getPagedReport(PagedQueryItem<Map> queryItem);

    /**
     * 分页查询用户报表列表
     * @param resId
     * @return Result<T>.class
     */
    Result getReportDetail(String resId);

    /**
     * 查询报表信息及权限
     * @param businessLine
     * @return Result<T>.class
     */
    Result getReportCommonPermission(String businessLine);


}
