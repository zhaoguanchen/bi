package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;

import java.util.Map;

public interface WidgetPageService {

    /**
     * 查询图表添加等权限
     *
     * @param businessLine
     * @return
     */
    Result getWidgetCommonPermission(String businessLine);

    /**
     * 分页查询用户图表列表
     *
     * @param queryItem
     * @return
     */
    Result getPagedWidget(PagedQueryItem<Map> queryItem);

    /**
     * 查询当前用户图表列表
     *
     * @param businessLine
     * @param search
     * @return
     */
    Result getWidgetList(String businessLine, String search);

    /**
     * 查询当前用户图表选择待选项
     *
     * @param businessLine
     * @param filter
     * @return
     */
    Result getWidgetSelector(String businessLine, String filter);
}
