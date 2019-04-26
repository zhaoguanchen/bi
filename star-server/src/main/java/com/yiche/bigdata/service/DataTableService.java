package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;

import java.util.List;

public interface DataTableService {

    /**
     * 列出改数据源下的事实表（无权限）
     * @param dataSourceId
     * @return Result<T>.class
     */
    Result listDataSourceDataTables(String dataSourceId);

    /**
     * 查询业务线所有事实表
     * @param businessLine
     * @return Result<T>.class
     */
    Result listBusinessLineDataTables(String businessLine);

    /**
     * 根据名称查询事实表
     * @param tableName
     * @return Result<T>.class
     */
    Result getMetadataTableInfo(String tableName);

    /**
     * 根据ID查询事实表
     * @param resId
     * @return Result<T>.class
     */
    Result getMetadataTableInfoById(String resId);

    /**
     * 业务线添加事实表目录
     * @param name
     * @param description
     * @param pid
     * @param businessLine
     * @return Result<T>.class
     */
    Result addDataTableDirectory(String name, String description, String pid, String businessLine);

    /**
     * 业务线添加事实表
     * @param dataSourceId
     * @param tableName
     * @param pid
     * @param businessLine
     * @return Result<T>.class
     */
    Result addDataTable(String dataSourceId, String tableName, String pid, String businessLine);

    /**
     * 对比删除事实表
     * @param tableKey
     * @param pid
     * @return Result<T>.class
     */
    Result deleteDataTable(List<String> tableKey, String pid);

    /**
     * 添加业务线资源
     * @param dataSourceId
     * @param tableName
     * @param pid
     * @param businessLine
     * @return Result<T>.class
     */
    Result addDataTable(String dataSourceId, List<String> tableName, String pid, String businessLine);

    /**
     * 通过Id删除事实表
     * @param resId
     * @return Result<T>.class
     */
    Result deleteDataTableById(String resId);

}
