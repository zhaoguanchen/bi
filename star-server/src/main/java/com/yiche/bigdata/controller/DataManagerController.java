package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.WidgetDataSearchItem;
import com.yiche.bigdata.service.DataManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("star/admin/data")
@Api(description = "数据查询、下载、下钻接口")
public class DataManagerController {

    @Autowired
    DataManagerService dataManagerService;

    @RequestMapping(value = "/getColumns")
    @ApiOperation("根据数据源和查询语句获取列参数")
    public Result getColumns(
            @ApiParam("数据源主键") @RequestParam(name = "datasourceId") String datasourceId,
            @ApiParam("查询串") @RequestParam(name = "query") String query,
            @ApiParam("是否重新加载") @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload
    ){
        return dataManagerService.getColumns(datasourceId,query,reload);
    }

    @RequestMapping(value = "/downloadToxls", method = RequestMethod.GET)
    @ApiOperation("根据主键下载数据")
    public ResponseEntity<byte[]> downloadExcel(@ApiParam("图表的主键") @RequestParam String resId){
        return dataManagerService.downloadExcel(resId);
    }

    @RequestMapping(value = "/getAggregateData",method = RequestMethod.POST)
    @ApiOperation("根据数据集获取图表数据")
    public Result getAggregateData(
            @ApiParam("数据集主键") @RequestParam(name = "datasetId") String datasetId,
            @ApiParam("时间主键") @RequestParam(name = "timeFilter", required = false) String timeFilterString,
            @ApiParam("是否环比") @RequestParam(name = "hasLinkRatio", required = false, defaultValue = "false") Boolean hasLinkRatio,
            @ApiParam("是否同比") @RequestParam(name = "hasYoYRatio", required = false, defaultValue = "false") Boolean hasYoYRatio,
            @ApiParam("过滤参数") @RequestParam(name = "cfg") String cfgString,
            @ApiParam("是否重新加载") @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload
    ){
        return dataManagerService.getAggregateData(datasetId,timeFilterString,hasLinkRatio,hasYoYRatio,cfgString,reload);
    }

    @RequestMapping(value = "/searchData",method = RequestMethod.POST)
    @ApiOperation("查询图表数据")
    public Result searchWidgetData(@RequestBody WidgetDataSearchItem dataSearchItem)
    {
        return dataManagerService.getWidgetData(dataSearchItem);
    }

    @ApiOperation("查询数据集指定列的所有值")
    @RequestMapping(value = "/getDimensionValues",method = RequestMethod.GET)
    public Result getDimensionValues(@RequestParam(name = "query", required = false) String query,
                                     @RequestParam(name = "datasetId", required = false) String datasetId,
                                     @RequestParam(name = "columnName", required = true) String columnName,
                                     @RequestParam(name = "cfg", required = false) String cfg,
                                     @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload) {
        return dataManagerService.getDimensionValues(query, datasetId, columnName, cfg, reload);
    }
}
