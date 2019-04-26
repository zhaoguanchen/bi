package com.yiche.bigdata.controller;

import com.yiche.bigdata.constants.PreviewType;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.DatasourceItem;
import com.yiche.bigdata.entity.vo.*;
import com.yiche.bigdata.service.DataSetPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("star/dataSet")
@Api(description = "数据集页面接口")
public class DataSetPageController {

    @Autowired
    private DataSetPageService dataSetPageService;

    @GetMapping(value = "/common/permission")
    @ApiOperation("查询数据集添加等权限")
    public Result<Map<String, String>> getReportCommonPermission(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return dataSetPageService.getDataSetCommonPermission(businessLine);
    }

    @PostMapping(value = "/list")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "pid :string")
    @ApiOperation("查询当前登录用户数据集列表")
    public Result<List<DataSetListVo>> getCurrentUserDataSet(@ApiParam("业务线ID") @RequestBody Map<String, Object> reqMap) {
        return dataSetPageService.getDataSetList((String) reqMap.get("pid"));
    }

    @ApiOperation(value = "分页查询用户报表列表", notes = "查询条件 condition { search:string, pid:string}")
    @PostMapping(value = "/list/paged")
    public Result<Pagination<CommonResourceVo>> getCurrentUserPagedReport(@ApiParam("业务线主键") @RequestBody PagedQueryItem<Map> queryItem) {
        return dataSetPageService.getPagedDataSet(queryItem);
    }

    @PostMapping(value = "/datasource/list")
    @ApiOperation("查询当前登录用户可用数据源")
    public Result<List<DatasourceItem>> listDataSource(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return dataSetPageService.listDataSource(businessLine);
    }

    @PostMapping(value = "/datasource/tables")
    @ApiOperation("查询当前登录用户数据源可用事实表")
    public Result<Map<String, List<DataTableSelectVo>>> listDataSource(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine,
                                                                       @ApiParam("数据源Id") @RequestParam("datasourceId") String datasourceId) {
        return dataSetPageService.listDataSourceTables(businessLine, datasourceId);
    }

    @PostMapping(value = "/datasource/tables/search")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "businessLine :string, datasourceId:string, condition:string")
    @ApiOperation("查询当前登录用户数据源可用事实表")
    public Result<Map<String, List<DataTableSelectVo>>> listDataSourceByName(@ApiParam("业务线主键") @RequestBody Map<String, Object> reqMap) {

        String businessLine = (String) reqMap.get("businessLine");
        String datasourceId = (String) reqMap.get("datasourceId");
        String condition = "";
        if ((String) reqMap.get("condition") != null) {
            condition = (String) reqMap.get("condition");
        }

        return dataSetPageService.listDataSourceTable(businessLine, datasourceId, condition);
    }

    @GetMapping(value = "/selector")
    @ApiOperation("查询当前用户数据集下拉菜单")
    public Result<List<ResourceSelectorVo>> getDataSetSelector(@ApiParam("父资源Id") @RequestParam("pid") String pid) {
        return dataSetPageService.getDataSetSelector(pid, null);
    }

    @PostMapping(value = "/columns")
    @ApiOperation("查询当前用户数据集列信息")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "dataSetIds:string[]")
    public Result<List<DataSetColumnsVo>> getDataSetColumns(@ApiParam("数据集Id列表") @RequestBody Map<String, Object> reqMap) {
        return dataSetPageService.getDataSetColumns((ArrayList) reqMap.get("dataSetIds"));
    }


    @PostMapping(value = "/list/search")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "pid :string, search:string")
    @ApiOperation("根据名称、创建人搜索数据集")
    public Result<List<DataSetListVo>> getDataSetSearchList(@ApiParam("业务线的ID和搜索条件") @RequestBody Map<String, Object> reqMap) {
        return dataSetPageService.getDataSetSearchList((String) reqMap.get("pid")
                , (String) reqMap.get("search"));
    }


    @PostMapping(value = "/viewDataSet")
    @ApiOperation("通过ID预览数据集")
    public Result<List<DataSetColumnsVo>> viewDataSetById(@ApiParam("数据集Id") @RequestParam("dateSetId") String resId) {
        return dataSetPageService.viewDataSet(resId, PreviewType.PREVIEW_BY_ID.value());
    }


    @PostMapping(value = "/viewDataSetByJson")
    @ApiOperation("通过JSON预览数据集")
    public Result<List<DataSetColumnsVo>> viewDataSetByJson(@ApiParam("数据集JSON") @RequestParam("dateSetId") String dateSetJson) {
        return dataSetPageService.viewDataSet(dateSetJson, PreviewType.PREVIEW_BY_JSON.value());
    }


    @PostMapping(value = "/validateExpression")
    @ApiOperation("自定义指标表达式校验")
    public Result validateExpression(@ApiParam("自定义指标表达式") @RequestBody ExpressionValidateVO expression) {
        return dataSetPageService.expressionValidator(expression);

    }
}
