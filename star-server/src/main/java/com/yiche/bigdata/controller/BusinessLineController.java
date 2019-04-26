package com.yiche.bigdata.controller;

import com.yiche.bigdata.annotation.SystemLog;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.DataTable;
import com.yiche.bigdata.entity.pojo.BusinessLineItem;
import com.yiche.bigdata.entity.pojo.MetadataTableInfoOptions;
import com.yiche.bigdata.entity.vo.BusinessLineVo;
import com.yiche.bigdata.entity.vo.DataSourceChooseVo;
import com.yiche.bigdata.service.BusinessLineService;
import com.yiche.bigdata.service.DataTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("star/admin/business_line")
@Api(description = "业务线管理接口")
public class BusinessLineController {

    @Autowired
    private BusinessLineService businessLineService;

    @Autowired
    private DataTableService dataTableService;

    @PostMapping(value = "/add")
    @ApiOperation("创建业务线")
    @SystemLog("创建业务线")
    public Result addBusinessLine(@ApiParam("业务线参数") @RequestBody BusinessLineItem<String> businessLineItem) {
        return businessLineService.addBusinessLine(businessLineItem);
    }

    @GetMapping(value = "/detail")
    @ApiOperation("查询业务线详细信息(包括事实表和数据源)")
    public Result<BusinessLineItem<DataTable>> searchDetailBusinessLine(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return businessLineService.getBusinessLineDetail(businessLine);
    }

    @PostMapping(value = "/update")
    @ApiOperation("根据Id更新业务线信息")
    @SystemLog("根据Id更新业务线信息")
    public Result updateBusinessLine(@ApiParam("业务线参数") @RequestBody BusinessLineItem<String> businessLineItem) {
        return businessLineService.updateBusinessLine(businessLineItem);
    }

    @PostMapping(value = "/list/paged")
    @ApiOperation("分页显示所有业务线列表（无权限控制）")
    public Result<BusinessLineVo> listPagedBusinessLine(@ApiParam("分页参数") @RequestBody PagedQueryItem<Map> queryItem) {
        return businessLineService.listBusinessLinePaged(queryItem);
    }

    @GetMapping(value = "/list")
    @ApiOperation("所有业务线列表（无权限控制）")
    public Result<BusinessLineVo> listBusinessLine() {
        return businessLineService.listBusinessLine();
    }


    @Deprecated
    @GetMapping(value = "/search")
    @ApiOperation("根据Id查询业务线")
    public Result getBusinessLine(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return businessLineService.getBusinessLineById(businessLine);
    }

    @GetMapping(value = "/delete")
    @ApiOperation("根据id删除业务线")
    @SystemLog("根据id删除业务线")
    public Result deleteBusinessLine(@ApiParam("业务线主键") @RequestParam String businessLine) {
        return businessLineService.deleteBusinessLine(businessLine);
    }


    @GetMapping(value = "/delete/force")
    @ApiOperation("强制删除业务线（包含空目录）")
    @SystemLog("强制删除业务线（包含空目录）")
    public Result deleteBusinessLineForce(@ApiParam("业务线主键") @RequestParam String businessLine) {
        return businessLineService.deleteBusinessLineForce(businessLine);
    }

    @GetMapping(value = "/list/department")
    @ApiOperation("查询业务线所有用户的一级部门列表")
    public Result<String> listDepartment(@ApiParam("业务线主键") @RequestParam String businessLine) {
        return businessLineService.getAllDepartment(businessLine);
    }

    @GetMapping(value = "/datasource/list")
    @ApiOperation("查询所有数据源")
    public Result<List<DataSourceChooseVo<MetadataTableInfoOptions>>> listAllDataSource(){
        return businessLineService.listAllDataSource();
    }

    @Deprecated
    @GetMapping(value = "/list/tables")
    @ApiOperation("查询业务线所有事实表")
    public Result<MetadataTableInfoOptions> listDataTables(@ApiParam("业务线主键") @RequestParam String businessLine) {
        return dataTableService.listBusinessLineDataTables(businessLine);
    }

    @Deprecated
    @ApiImplicitParam(name = "reqMap" , paramType = "body", dataType = "json", required = true
            , value = "name:string, description:string, pid:string, businessLine:string")
    @PostMapping(value = "/add/table/directory")
    @ApiOperation("业务线添加事实表目录")
    @SystemLog("业务线添加事实表目录")
    public Result addDatatableDirectory(@RequestBody Map<String,Object> reqMap) {
        return dataTableService.addDataTableDirectory((String)reqMap.get("name")
                , (String)reqMap.get("description")
                , (String)reqMap.get("pid"), (String)reqMap.get("businessLine"));
    }

    @Deprecated
    @ApiImplicitParam(name = "reqMap" , paramType = "body", dataType = "json", required = true
            , value = "dataSourceId:string, tableName:string, pid:string, businessLine:string")
    @PostMapping(value = "/add/table")
    @ApiOperation("业务线添加事实表")
    @SystemLog("业务线添加事实表")
    public Result addDataTable(@RequestBody Map<String,Object> reqMap) {
        return dataTableService.addDataTable((String)reqMap.get("dataSourceId")
                , (String)reqMap.get("tableName")
                , (String)reqMap.get("pid"), (String)reqMap.get("businessLine"));
    }

    @Deprecated
    @GetMapping(value = "/delete/table")
    @ApiOperation("通过Id删除事实表")
    @SystemLog("通过Id删除事实表")
    public Result deleteDataTable(@ApiParam("事实表主键") @RequestParam String resId) {
        return dataTableService.deleteDataTableById(resId);
    }
}
