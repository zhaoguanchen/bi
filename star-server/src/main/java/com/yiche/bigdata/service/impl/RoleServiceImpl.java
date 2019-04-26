package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.constants.RoleType;
import com.yiche.bigdata.dao.RoleDao;
import com.yiche.bigdata.dao.UserRoleRelDao;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.generated.UserRoleRel;
import com.yiche.bigdata.entity.vo.DomainUserInfoVo;
import com.yiche.bigdata.service.BusinessLineService;
import com.yiche.bigdata.service.LogService;
import com.yiche.bigdata.service.RoleService;
import com.yiche.bigdata.service.UserInfoService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.PaginationUtil;
import com.yiche.bigdata.utils.ResultUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRoleRelDao userRoleRelDao;

    @Autowired
    private LogService logService;

    @Autowired
    private BusinessLineService businessLineService;

    @Override
    public Result addRoleToBusinessLine(String roleName, String description, String businessLine) {
        if (StringUtils.isEmpty(roleName) || StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Result result = businessLineService.getBusinessLineById(businessLine);
        if (ResultCode.OK.value() != result.getCode()) {
            return result;
        }
        if (roleDao.findRole(roleName, businessLine) != null) {
            return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_ROLE_EXIST);
        }
        Role role = new Role();
        role.setId(CommonUtils.getUUID());
        role.setName(roleName);
        role.setDescription(description);
        role.setBusinessLine(businessLine);
        role.setType(RoleType.BUSINESS_TYPE.value());
        String operator = userInfoService.getUserNameByToken(TokenUtils.getToken());
        role.setCreater(operator);
        role.setCreateTime(new Date());
        role.setLastModifier(operator);
        role.setLastModifyTime(new Date());
        if (roleDao.addRole(role)) {
            logService.saveRole(role);
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    @Override
    public Result addBusinessLineAdminRole(String businessLine, String businessLineName) {
        String roleName = String.format("%s_管理员", businessLineName);
        Role role = new Role();
        role.setId(CommonUtils.getUUID());
        role.setName(roleName);
        role.setBusinessLine(businessLine);
        role.setType(RoleType.BUSINESS_ADMIN_TYPE.value());
        String operator = userInfoService.getUserNameByToken(TokenUtils.getToken());
        role.setDescription(roleName);
        role.setCreater(operator);
        role.setCreateTime(new Date());
        role.setLastModifier(operator);
        role.setLastModifyTime(new Date());
        if (roleDao.addRole(role)) {
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    @Override
    public Result deleteRole(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (roleDao.findRoleById(roleId) == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }
        if (roleDao.deleteRole(roleId)) {
            logService.deleteRole(roleId);
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    @Override
    public Result findRoleById(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        } else {
            return ResultUtils.buildResult(ResultCode.OK, role);
        }
    }

    @Override
    public Result updateRoleById(String roleId, String roleName, String description) {
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(roleName)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (roleDao.findRoleById(roleId) == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }

        Role template = new Role();
        template.setId(roleId);
        template.setName(roleName);
        template.setDescription(description);
        template.setLastModifier(userInfoService.getUserNameByToken(TokenUtils.getToken()));
        template.setLastModifyTime(new Date());

        if (roleDao.updateRoleById(template)) {
            logService.updateRole(template);
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    @Override
    public Result listBusinessLineRole(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<Role> roleList = roleDao.listRole(businessLine);
        if (CollectionUtils.isEmpty(roleList)) {
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        } else {
            return ResultUtils.buildResult(ResultCode.OK, roleList);
        }
    }

    @Override
    public Result listPagedBusinessLineRole(PagedQueryItem<Map> pagedQueryItem) {
        if (pagedQueryItem == null) {
            ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (CollectionUtils.isEmpty(pagedQueryItem.getCondition())) {
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
        int total = (int) roleDao.countUser(pagedQueryItem.getCondition());
        if (total == 0) {
            ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
        List<Role> roleList = roleDao.listPagedRole(pagedQueryItem);
        Pagination<Role> pageResult = new Pagination<>(total, pagedQueryItem.getPageNo(),
                PaginationUtil.totalPage(total, pagedQueryItem.getPageSize()), roleList);
        return ResultUtils.buildResult(ResultCode.OK, pageResult);
    }

    @Override
    public Result listRoleUsers(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<UserRoleRel> relList = userRoleRelDao.findUsersByRoleId(roleId);
        if (relList == null) {
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        } else {
            List<DomainUserInfoVo> voList = new ArrayList<>();
            for (UserRoleRel rel : relList) {
                DomainUserInfo userInfo = userInfoService.getUserInfoByUserName(rel.getUserId());
                if (userInfo == null) {
                    continue;
                }
                DomainUserInfoVo domainUserInfoVo = new DomainUserInfoVo(userInfo);
                voList.add(domainUserInfoVo);
            }
            return ResultUtils.buildResult(ResultCode.OK, voList);
        }
    }
}
