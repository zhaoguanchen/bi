package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.FilterComponent;
import com.yiche.bigdata.entity.pojo.RowPermissionItem;

import java.util.List;

public interface RowPermissionService {

    /**
     * 查询资源在当前角色的权限
     * @param roleId
     * @param resId
     * @return
     */
    List<FilterComponent> findRowPermission(String roleId, String resId);

    /**
     * 列出数据集字段接口
     * @param resId
     * @return
     */
    Result listColumns(String resId);

    /**
     * 保存行级权限
     * @param rowPermissionItem
     * @return
     */
    Result saveRowPermission(RowPermissionItem rowPermissionItem);

    /**
     * 查询数据集行级权限
     * @param roleId
     * @param resId
     * @return
     */
    Result listDataSetRowPermission(String roleId, String resId);
}
