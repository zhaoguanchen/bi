package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.pojo.PermissionPageSaveItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

public interface LogService {

    /**
     * 保存新用户
     *
     * @param userInfo
     */
    void saveNewUser(DomainUserInfo userInfo);

    /**
     * 保存角色
     *
     * @param role
     */
    void saveRole(Role role);

    /**
     * 删除角色
     *
     * @param roleId
     */
    void deleteRole(String roleId);

    /**
     * 更新角色
     *
     * @param role
     */
    void updateRole(Role role);

    /**
     * 更新用户角色关系
     *
     * @param userName
     * @param roleIdList
     * @param businessLine
     */
    void updateUserRole(String userName, List roleIdList, String businessLine);

    /**
     * 删除用户角色关系
     *
     * @param userName
     * @param roleId
     */
    void deleteUserRoles(String userName, String roleId);

    /**
     * 更新角色资源
     *
     * @param roleId
     * @param permissionLog
     */
    void updateRoleRes(String roleId, List<List<String>> permissionLog);

    /**
     * 下载excel
     *
     * @param wb
     * @param time
     */
    void tableToxls(HSSFWorkbook wb, long time);
}
