package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;

import java.util.Map;

public interface RoleService {

    /**
     * 添加角色
     * @param roleName
     * @param description
     * @param businessLine
     * @return Result<T>.class
     */
    Result addRoleToBusinessLine(String roleName, String description, String businessLine);

    /**
     * 为业务线创建管理员角色
     * @param businessLine
     * @param businessLineName
     * @return Result<T>.class
     */
    Result addBusinessLineAdminRole(String businessLine, String businessLineName);

    /**
     * 通过ID删除角色
     * @param roleId
     * @return Result<T>.class
     */
    Result deleteRole(String roleId);

    /**
     * 通过ID查询角色
     * @param roleId
     * @return Result<T>.class
     */
    Result findRoleById(String roleId);

    /**
     * 通过ID更新角色信息
     * @param roleId
     * @param roleName
     * @param description
     * @return Result<T>.class
     */
    Result updateRoleById(String roleId, String roleName, String description);

    /**
     * 查询业务线下所有角色
     * @param businessLine
     * @return Result<T>.class
     */
    Result listBusinessLineRole(String businessLine);

    /**
     * 分页查询业务线下角色列表
     * @param pagedQueryItem
     * @return Result<T>.class
     */
    Result listPagedBusinessLineRole(PagedQueryItem<Map> pagedQueryItem);

    /**
     * 查询使用该角色的所有用户
     * @param roleId
     * @return Result<T>.class
     */
    Result listRoleUsers(String roleId);
}
