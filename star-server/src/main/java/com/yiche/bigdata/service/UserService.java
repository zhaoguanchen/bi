package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.vo.UserRoleVo;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 业务线添加用户
     *
     * @param userName
     * @param businessLine
     * @return
     */
    Result addUserToBusinessLine(String userName, String businessLine);

    /**
     * 业务线删除用户
     *
     * @param userName
     * @param businessLine
     * @return
     */
    Result deleteUserFromBusinessLine(String userName, String businessLine);

    /**
     * 查询用户在该业务线下所有角色
     *
     * @param userName
     * @param businessLine
     * @return
     */
    Result listBusinessLineRole(String userName, String businessLine);

    /**
     * 分页查询业务线下用户列表
     *
     * @param pagedQueryItem
     * @return
     */
    Result listPagedBusinessLineUser(PagedQueryItem<Map> pagedQueryItem);

    /**
     * 用户添加角色列表
     * @param userName
     * @param roleList
     * @param businessLine
     * @return
     */
    Result addRoleToUser(String userName, List<UserRoleVo> roleList, String businessLine);


    /**
     * 用户删除角色
     *
     * @param userName
     * @param roleId
     * @return
     */
    Result deleteRoleForUser(String userName, String roleId);

}
