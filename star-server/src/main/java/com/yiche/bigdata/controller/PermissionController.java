package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.DatasourceItem;
import com.yiche.bigdata.entity.pojo.PermissionPageSaveItem;
import com.yiche.bigdata.entity.vo.DataResPermissionVo;
import com.yiche.bigdata.entity.vo.DataTableCategoryPermissionVo;
import com.yiche.bigdata.entity.vo.PermissionVo;
import com.yiche.bigdata.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("star/admin/permission")
@Api(description = "权限管理接口")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "roleId:string, resType:string")
    @PostMapping(value = "/list/resource/detail")
    @ApiOperation("列出业务线可用资源及权限（数据集、图表、报表）")
    public Result<DataResPermissionVo> listRoleDataResources(@ApiParam("用户角色ID和资源类型") @RequestBody Map<String, Object> reqMap) {
        return permissionService.listRoleDataResourcePermission((String) reqMap.get("roleId"),
                (String) reqMap.get("resType"));
    }

    @GetMapping(value = "/list/dashboard/detail")
    @ApiOperation("列出业务线大盘权限")
    public Result<DataResPermissionVo> listRoleDashboardPermission(@ApiParam("用户角色ID") @RequestParam("roleId") String roleId) {
        return permissionService.listRoleDashboardPermission(roleId);
    }

    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "roleId:string, datasourceId:string")
    @PostMapping(value = "/list/table/detail")
    @ApiOperation("列出业务线事实表资源及权限")
    public Result<List<DataTableCategoryPermissionVo>> listRoleDataTablePermission(@ApiParam("用户角色Id和数据源id") @RequestBody Map<String, Object> reqMap) {
        return permissionService.listRoleDataTablePermission((String) reqMap.get("roleId"),
                (String) reqMap.get("datasourceId"));
    }

    @PostMapping(value = "/add")
    @ApiOperation("设置资源权限接口")
    public Result resetResourcePermission(@ApiParam("要添加的权限信息列表") @RequestBody PermissionPageSaveItem permissionPageSaveItem) {
        return permissionService.resetResourcePermission(permissionPageSaveItem);
    }

    @GetMapping(value = "/datasource/list")
    @ApiOperation("查询业务线可用数据源")
    public Result<DatasourceItem> listRoleDataTablePermission(@ApiParam("用户角色id") @RequestParam("roleId") String roleId) {
        return permissionService.listDataSource(roleId);
    }

    @PostMapping(value = "/datasource/permission/update")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "roleId:string, datasourceId:string, checked:boolean")
    @ApiOperation("设置业务线数据源权限")
    public Result updateDatasourcePermission(@ApiParam("用户角色id和数据源id和是否选中") @RequestBody Map<String, Object> reqMap) {
        return permissionService.updateDatasourcePermission((String) reqMap.get("roleId"),
                (String) reqMap.get("datasourceId"),
                (Boolean) reqMap.get("checked"));
    }

    @PostMapping(value = "/datasource/permission/list")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "roleId:string, datasourceId:string")
    @ApiOperation("查询业务线数据源权限（mysql）")
    public Result<PermissionVo> getDatasourcePermission(@ApiParam("用户角色id和数据源id") @RequestBody Map<String, Object> reqMap) {
        return permissionService.getDatasourcePermission((String) reqMap.get("roleId"),
                (String) reqMap.get("datasourceId"));
    }
}
