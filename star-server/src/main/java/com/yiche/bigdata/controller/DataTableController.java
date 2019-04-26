package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.MetadataTableInfoOptions;
import com.yiche.bigdata.service.DataTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("star/admin/table")
@Api(description = "事实表信息查询接口")
public class DataTableController {

    @Autowired
    private DataTableService dataTableService;

    @GetMapping(value = "/list/datasource")
    @ApiOperation("列出改数据源下的事实表（无权限）")
    public Result<List<MetadataTableInfoOptions>> listDatasourceDataTables(@ApiParam("数据源主键") @RequestParam("dataSourceId") String dataSourceId) {
        return dataTableService.listDataSourceDataTables(dataSourceId);
    }

    @GetMapping(value = "/search/name")
    @ApiOperation("根据名称查询事实表")
    public Result<MetadataTableInfoOptions> getDataTableByTableName(@ApiParam("事实表名称") @RequestParam("tableName") String tableName) {
        return dataTableService.getMetadataTableInfo(tableName);
    }

    @GetMapping(value = "/search/id")
    @ApiOperation("根据ID查询事实表")
    public Result<MetadataTableInfoOptions> getDataTableById(@ApiParam("事实表主键") @RequestParam("resId") String resId) {
        return dataTableService.getMetadataTableInfoById(resId);
    }


}
