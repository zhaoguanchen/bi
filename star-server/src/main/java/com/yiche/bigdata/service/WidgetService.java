package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.WidgetVO;

public interface WidgetService {

    /**
     * 根据主键查询一条图表数据
     *
     * @param resId
     * @return Result<T>.class
     */
    Result getWidgetByResId(String resId);

    /**
     * 新增图表目录，同时新增资源树
     *
     * @param directoryVO
     * @return Result<T>.class
     */
    Result addWidgetDirectory(DirectoryVO directoryVO);

    /**
     * 新增图表，同时新增资源树
     *
     * @param widgetVO
     * @return Result<T>.class
     */
    Result addWidget(WidgetVO widgetVO);

    /**
     * 修改图表文件夹名称，同时修改资源树
     *
     * @param directoryVO
     * @return
     */
    Result updateWidgetDirectory(DirectoryVO directoryVO);

    /**
     * 复制图表，同时新增资源树
     *
     * @param resId
     * @return Result<T>.class
     */
    Result copyWidget(String resId);

    /**
     * 移动图表，同时修改资源树
     *
     * @param resId
     * @return Result<T>.class
     */
    Result moveWidget(String resId, String targetDirectoryId);

    /**
     * 修改图表，同时修改资源树
     *
     * @param widget
     * @return Result<T>.class
     */
    Result updateWidget(Widget widget);

    /**
     * 删除图表，同时删除资源树
     *
     * @param resId
     * @return Result<T>.class
     */
    Result deleteWidget(String resId);

    /**
     * 删除文件夹，同时删除资源树
     *
     * @param resId
     * @return
     */
    Result deleteDirectory(String resId);

}
