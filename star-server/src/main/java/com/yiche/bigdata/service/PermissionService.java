package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.PermissionPageSaveItem;
import com.yiche.bigdata.entity.pojo.PermissionSaveItem;

import java.util.List;

public interface PermissionService {

    /**
     * 列出业务线可用资源及权限（数据集、图表、报表）
     * @param roleId
     * @param resType
     * @return Result<T>.class
     */
    Result listRoleDataResourcePermission(String roleId, String resType);

    /**
     * 列出业务线大盘权限
     * @param roleId
     * @param datasourceId
     * @return Result<T>.class
     */
    Result listRoleDataTablePermission(String roleId, String datasourceId);

    /**
     * 列出业务线事实表资源及权限
     * @param roleId
     * @return Result<T>.class
     */
    Result listRoleDashboardPermission(String roleId);

    /**
     * 设置资源权限接口
     * @param permissionPageSaveItem
     * @return Result<T>.class
     */
    Result resetResourcePermission(PermissionPageSaveItem permissionPageSaveItem);

    /**
     * 查询业务线可用数据源
     * @param roleId
     * @return Result<T>.class
     */
    Result listDataSource(String roleId);

    /**
     * 设置业务线数据源权限
     * @param roleId
     * @param datasourceId
     * @param checked
     * @return Result<T>.class
     */
    Result updateDatasourcePermission(String roleId, String datasourceId, Boolean checked);

    /**
     * 查询业务线数据源权限（mysql）
     * @param roleId
     * @param datasourceId
     * @return Result<T>.class
     */
    Result getDatasourcePermission(String roleId, String datasourceId);

}
