package com.yiche.bigdata.controller;

import com.yiche.bigdata.annotation.SystemLog;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.pojo.UserRoleItem;
import com.yiche.bigdata.entity.vo.DomainUserInfoVo;
import com.yiche.bigdata.service.UserService;
import com.yiche.bigdata.service.impl.UserInfoServiceImpl;
import com.yiche.bigdata.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("star/admin/user")
@Api(description = "用户管理接口")
public class UserController {

    @Autowired
    private UserInfoServiceImpl userInfoService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/check")
    @ApiOperation("根据域账号查询用户信息")
    public Result<DomainUserInfoVo> getDomainUserInfo(@ApiParam("用户的域账号") @RequestParam("user") final String username) {
        DomainUserInfo domainUserInfo = userInfoService.getUserInfoByUserName(username);
        if (domainUserInfo != null) {
            return ResultUtils.buildResult(ResultCode.OK, new DomainUserInfoVo(domainUserInfo));
        } else {
            return ResultUtils.buildResult(ResultCode.USER_NAME_INVALID);
        }
    }

    @GetMapping(value = "/search")
    @ApiOperation("模糊查询域账号")
    public Result<List<DomainUserInfoVo>> searchDomainUser(@ApiParam("查询条件") @RequestParam("search") final String username) {
        List<DomainUserInfo> accountList = userInfoService.searchDomainUser(username);
        List<DomainUserInfoVo> resultVo = new ArrayList<>();
        if (!CollectionUtils.isEmpty(accountList)) {
            accountList.forEach(user -> resultVo.add(new DomainUserInfoVo(user)));
        }
        return ResultUtils.buildResult(ResultCode.OK, resultVo);
    }

    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "userName:string, businessLine:string")
    @PostMapping(value = "/add")
    @ApiOperation("业务线添加用户")
    @SystemLog("业务线添加用户")
    public Result addUserToBusinessLine(@ApiParam("用户名称和业务线id") @RequestBody Map<String, Object> reqMap) {
        return userService.addUserToBusinessLine((String) reqMap.get("userName")
                , (String) reqMap.get("businessLine"));

    }

    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "userName:string, businessLine:string")
    @PostMapping(value = "/delete")
    @ApiOperation("业务线删除用户")
    @SystemLog("业务线删除用户")
    public Result deleteUserFromBusinessLine(@ApiParam("用户名和业务线id") @RequestBody Map<String, Object> reqMap) {
        return userService.deleteUserFromBusinessLine((String) reqMap.get("userName")
                , (String) reqMap.get("businessLine"));

    }

    @ApiOperation(value = "分页查询业务线下用户列表", notes = "查询条件 condition { nameSearch:string, department:string, businessLine:string }")
    @PostMapping(value = "/list/paged")
    public Result<Pagination<DomainUserInfoVo>> listBusinessLineUser(@ApiParam("查询条件") @RequestBody PagedQueryItem<Map> queryItem) {
        return userService.listPagedBusinessLineUser(queryItem);
    }

    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "userName:string, roleArr:UserRoleVo[], businessLine:string")
    @PostMapping(value = "/add/roles")
    @ApiOperation("用户添加角色列表")
    @SystemLog("用户添加角色列表")
    public Result addRoleToUser(@ApiParam("用户名和要添加的角色id数组和业务线") @RequestBody UserRoleItem reqMap) {

        return userService.addRoleToUser(reqMap.getUserName(),
                reqMap.getArr(),
                reqMap.getBusinessLine());
    }

    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "userName:string, roleId:string")
    @PostMapping(value = "/delete/role")
    @ApiOperation("用户删除角色")
    @SystemLog("用户删除角色")
    public Result deleteRoleForUser(@ApiParam("用户名和用户角色id") @RequestBody Map<String, Object> reqMap) {
        return userService.deleteRoleForUser((String) reqMap.get("userName"),
                (String) reqMap.get("roleId"));
    }


    @PostMapping(value = "/list/role")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "userName:string, businessLine:string")
    @ApiOperation("查询用户在该业务线下所有角色")
    public Result<List<Role>> listBusinessLineUser(@ApiParam("用户名和业务线id") @RequestBody Map<String, Object> reqMap) {
        return userService.listBusinessLineRole((String) reqMap.get("userName")
                , (String) reqMap.get("businessLine"));
    }
}
