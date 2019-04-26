package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.RowPermissionItem;
import com.yiche.bigdata.entity.pojo.RowPermissionOptionItem;
import com.yiche.bigdata.entity.vo.TableColumnInfoVo;
import com.yiche.bigdata.service.RowPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("star/admin/rowPermission")
@Api(description = "行级权限管理接口")
public class RowPermissionController {

    @Autowired
    private RowPermissionService rowPermissionService;

    @GetMapping(value = "/columns/list")
    @ApiOperation("列出数据集字段接口")
    public Result<List<TableColumnInfoVo>> listColumns(@ApiParam("数据集主键") @RequestParam("resId")  String resId){
        return rowPermissionService.listColumns(resId);
    }

    @PostMapping(value = "/save")
    @ApiOperation("保存行级权限")
    public Result updateRoleById(@ApiParam("行及权限参数") @RequestBody RowPermissionItem rowPermissionItem) {
        return rowPermissionService.saveRowPermission(rowPermissionItem);
    }

    @ApiImplicitParam(name = "reqMap" , paramType = "body", dataType = "json", required = true
            , value = "roleId:string, resId:string")
    @PostMapping(value = "/list")
    @ApiOperation("查询数据集行级权限")
    public Result<List<RowPermissionOptionItem>> listDataSetRowPermission(@ApiParam("用户角色id和行及权限id") @RequestBody Map<String,Object> reqMap) {
        return rowPermissionService.listDataSetRowPermission((String)reqMap.get("roleId")
                , (String)reqMap.get("resId"));
    }

}
