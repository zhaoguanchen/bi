package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.BusinessLineUserDao;
import com.yiche.bigdata.dao.UserRoleRelDao;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.BusinessLineUser;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.generated.UserRoleRel;
import com.yiche.bigdata.entity.vo.DomainUserInfoVo;
import com.yiche.bigdata.entity.vo.UserRoleVo;
import com.yiche.bigdata.service.LogService;
import com.yiche.bigdata.service.RoleService;
import com.yiche.bigdata.service.UserInfoService;
import com.yiche.bigdata.service.UserService;
import com.yiche.bigdata.utils.PaginationUtil;
import com.yiche.bigdata.utils.ResultUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private BusinessLineUserDao businessLineUserDao;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRelDao userRoleRelDao;

    @Autowired
    private LogService logService;

    @Override
    public Result addUserToBusinessLine(String userName, String businessLine) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        DomainUserInfo userInfo = userInfoService.getUserInfoByUserName(userName);
        if (userInfo == null) {
            return ResultUtils.buildResult(ResultCode.USER_NAME_INVALID);
        }
        if (businessLineUserDao.findUser(userName, businessLine) != null) {
            return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_USER_EXIST);
        }
        BusinessLineUser businessLineUser = new BusinessLineUser();
        BeanUtils.copyProperties(userInfo, businessLineUser);
        businessLineUser.setBusinessLine(businessLine);
        businessLineUser.setCreater(userInfoService.getUserNameByToken(TokenUtils.getToken()));
        businessLineUser.setCreateTime(new Date());
        if (businessLineUserDao.addUser(businessLineUser)) {
            logService.saveNewUser(userInfo);
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    @Override
    public Result deleteUserFromBusinessLine(String userName, String businessLine) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (businessLineUserDao.findUser(userName, businessLine) == null) {
            return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_USER_NOT_EXIST);
        }
        if (businessLineUserDao.deleteUser(userName, businessLine)) {
            deleteUserRelations(userName, businessLine);
            return ResultUtils.buildResult(ResultCode.OK);
        }
        return ResultUtils.buildResult(ResultCode.DELETE_BUSINESS_LINE_USER_FAILURE);
    }

    @Override
    public Result<Pagination<DomainUserInfoVo>> listPagedBusinessLineUser(PagedQueryItem<Map> pagedQueryItem) {
        if (CollectionUtils.isEmpty(pagedQueryItem.getCondition())) {
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
        int total = (int) businessLineUserDao.countUser(pagedQueryItem.getCondition());
        if (total == 0) {
            ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
        List<BusinessLineUser> userList = businessLineUserDao.listPagedUser(pagedQueryItem);
        List<DomainUserInfoVo> volist = new ArrayList<>();
        userList.stream().forEach((user) -> volist.add(new DomainUserInfoVo(user)));
        Pagination<BusinessLineUser> pageResult = new Pagination<>(total, pagedQueryItem.getPageNo(),
                PaginationUtil.totalPage(total, pagedQueryItem.getPageSize()), userList);
        return ResultUtils.buildResult(ResultCode.OK, pageResult);
    }

    @Override
    public Result addRoleToUser(String userName, List<UserRoleVo> roleList, String businessLine) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(businessLine) || roleList == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }

        for (UserRoleVo roleItem : roleList) {
            Result roleResult = roleService.findRoleById(roleItem.getId());
            if (roleResult.getCode() != ResultCode.OK.value()) {
                return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_ROLE_EXIST);
            } else {
                Role role = (Role) roleResult.getResult();
                if (role == null) {
                    return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
                } else if (!role.getBusinessLine().equals(businessLine)) {
                    return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST,
                            String.format("角色：%s 不属于此业务线", role.getId()));
                }
            }
        }

        deleteUserRelations(userName, businessLine);
        List<String> idList = new ArrayList<>();
        for (UserRoleVo roleVo : roleList) {
            if (roleVo.isChecked()) {
                userRoleRelDao.add(userName, roleVo.getId());
            }
            idList.add(roleVo.getId());
        }


        logService.updateUserRole(userName, idList, businessLine);

        return ResultUtils.buildResult(ResultCode.OK);
    }

    private boolean deleteUserRelations(String userName, String businessLine) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(businessLine)) {
            return false;
        }
        Result searchRoleResult = roleService.listBusinessLineRole(businessLine);
        if (searchRoleResult.getCode() == ResultCode.OK.value()) {
            List<String> roleIdList = new ArrayList<>();
            ((List) searchRoleResult.getResult()).stream().forEach((role) ->
                    roleIdList.add(((Role) role).getId()));
            return userRoleRelDao.delete(userName, roleIdList);
        }
        return false;
    }

    @Override
    public Result deleteRoleForUser(String userName, String roleId) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(roleId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (userRoleRelDao.delete(userName, roleId)) {
            logService.deleteUserRoles(userName, roleId);
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DELETE_DATA_FAILURE);
        }
    }

    @Override
    public Result listBusinessLineRole(String userName, String businessLine) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Result roleListResult = roleService.listBusinessLineRole(businessLine);
        if (roleListResult.getCode() != ResultCode.OK.value()) {
            return roleListResult;
        }
        List<Role> roleList = (List<Role>) roleListResult.getResult();
        List<UserRoleRel> userRoleRelList = userRoleRelDao.listUserRoles(userName);
        List<String> userRoleIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userRoleRelList)) {
            for (UserRoleRel userRoleRel : userRoleRelList) {
                userRoleIdList.add(userRoleRel.getRoleId());
            }
        }
        List<UserRoleVo> resultVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleList)) {
            for (Role role : roleList) {
                if (role != null) {
                    UserRoleVo userRoleVo = new UserRoleVo();
                    BeanUtils.copyProperties(role, userRoleVo);
                    if (userRoleIdList.contains(role.getId())) {
                        userRoleVo.setChecked(true);
                    }
                    resultVoList.add(userRoleVo);
                }
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, resultVoList);
    }
}
