package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Report;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.ReportVO;
import com.yiche.bigdata.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("star/admin/report")
@Api(description = "报表数据、报表目录管理接口")
public class ReportController {

    @Autowired
    ReportService reportService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiOperation("根据主键查询一条报表数据")
    public Result<Report> getReportByResId(@ApiParam("报表主键") @RequestParam String resId) {
        return reportService.getReportByResId(resId);
    }

    @RequestMapping(value = "/addDirectory", method = RequestMethod.POST)
    @ApiOperation("新增报表目录，同时新增资源树")
    public Result addReportDirectory(@ApiParam("报表目录参数") @RequestBody DirectoryVO directoryVO) {
        return reportService.addReportDirectory(directoryVO);
    }

    @RequestMapping(value = "/updateDirectory", method = RequestMethod.POST)
    @ApiOperation("更新报表目录，同时更新资源树")
    public Result updateReportDirectory(@ApiParam("报表目录参数") @RequestBody DirectoryVO directoryVO) {
        return reportService.addReportDirectory(directoryVO);
    }


    @RequestMapping(value = "/deleteDirectory", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string")
    @ApiOperation("删除目录，同时删除资源树")
    public Result deleteDirectory(@ApiParam("文件夹的主键") @RequestBody Map<String, Object> reqMap) {
        return reportService.deleteReportDirectory((String) reqMap.get("resId"));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("新增报表，同时新增资源树")
    public Result addReport(@ApiParam("报表参数") @RequestBody ReportVO reportVO) {
        return reportService.addReport(reportVO);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string")
    @ApiOperation("复制报告，同时新增资源树")
    public Result copyReport(@ApiParam("图表主键") @RequestBody Map<String, Object> reqMap) {
        return reportService.copyReport((String) reqMap.get("resId"));
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string, targetDirectoryId:string")
    @ApiOperation("移动报告，同时修改资源树")
    public Result moveWidget(@ApiParam("报告主键") @RequestBody Map<String, Object> reqMap) {
        return reportService.moveReport((String) reqMap.get("resId"), (String) reqMap.get("targetDirectoryId"));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation("修改报表，同时修改资源树")
    public Result updateReport(@ApiParam("报表参数") @RequestBody Report report) {
        return reportService.updateReport(report);
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string")
    @ApiOperation("删除报告，同时删除资源树")
    public Result deleteWidget(@ApiParam("图表的主键") @RequestBody Map<String, Object> reqMap) {
        return reportService.deleteReport((String) reqMap.get("resId"));
    }


}
