package com.yiche.bigdata.controller;

import com.yiche.bigdata.annotation.SystemLog;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.vo.DomainUserInfoVo;
import com.yiche.bigdata.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("star/admin/role")
@Api(description = "角色管理接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiImplicitParam(name = "reqMap" , paramType = "body", dataType = "json", required = true
            , value = "roleName:string, description:string, businessLine:string")
    @PostMapping(value = "/add")
    @ApiOperation("添加角色")
    @SystemLog("添加角色")
    public Result addRoleToBusinessLine(@ApiParam("要添加的角色信息") @RequestBody Map<String,Object> reqMap) {
        return roleService.addRoleToBusinessLine((String)reqMap.get("roleName")
                , (String)reqMap.get("description")
                , (String)reqMap.get("businessLine"));

    }

    @GetMapping(value = "/delete")
    @ApiOperation("通过ID删除角色")
    @SystemLog("通过ID删除角色")
    public Result deleteRole(@ApiParam("用户角色主键") @RequestParam("roleId") String roleId) {
        return roleService.deleteRole(roleId);
    }

    @GetMapping(value = "/search")
    @ApiOperation("通过ID查询角色")
    public Result searchRoleById(@ApiParam("用户角色主键") @RequestParam("roleId") String roleId) {
        return roleService.findRoleById(roleId);
    }

    @ApiImplicitParam(name = "reqMap" , paramType = "body", dataType = "json", required = true
            , value = "roleId:string, roleName:string, description:string")
    @PostMapping(value = "/update")
    @ApiOperation("通过ID更新角色信息")
    @SystemLog("通过ID更新角色信息")
    public Result updateRoleById(@ApiParam("角色参数信息") @RequestBody Map<String,Object> reqMap) {
        return roleService.updateRoleById((String)reqMap.get("roleId")
                , (String)reqMap.get("roleName")
                , (String)reqMap.get("description"));
    }

    @PostMapping(value = "/list")
    @ApiOperation("查询业务线下所有角色")
    public Result<List<Role>> listBusinessLineUser(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return roleService.listBusinessLineRole(businessLine);
    }

    @ApiOperation(value="分页查询业务线下角色列表", notes="查询条件 condition { nameSearch:string, businessLine:string }")
    @PostMapping(value = "/list/paged")
    public Result<Pagination<Role>> listPagedBusinessLineUser(@ApiParam("查询条件") @RequestBody PagedQueryItem<Map> queryItem) {
        return roleService.listPagedBusinessLineRole(queryItem);
    }

    @GetMapping(value = "/list/user")
    @ApiOperation("查询使用该角色的所有用户")
    public Result<List<DomainUserInfoVo>> listRoleUsers(@ApiParam("用户角色主键") @RequestParam("roleId") String roleId) {
        return roleService.listRoleUsers(roleId);
    }
}
