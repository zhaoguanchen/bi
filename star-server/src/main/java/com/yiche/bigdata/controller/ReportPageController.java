package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.vo.CommonResourceVo;
import com.yiche.bigdata.entity.vo.ReportPermissionVo;
import com.yiche.bigdata.service.ReportPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("star/report")
@Api(description = "报表页面接口")
public class ReportPageController {

    @Autowired
    ReportPageService reportPageService;

    @GetMapping(value = "/common/permission")
    @ApiOperation("查询报表添加等权限")
    public Result<Map<String, String>> getReportCommonPermission(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return reportPageService.getReportCommonPermission(businessLine);
    }


    @PostMapping(value = "/list")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "pid :string, search:string")
    @ApiOperation("查询当前登录报表列表")
    public Result<List<CommonResourceVo>> getCurrentUserReport(@ApiParam("所在业务线ID,搜索条件") @RequestBody Map<String, Object> reqMap) {
        return reportPageService.getReportList((String) reqMap.get("pid")
                , (String) reqMap.get("search"));
    }

    @ApiOperation(value = "分页查询用户报表列表", notes = "查询条件 condition { search:string, pid:string}")
    @PostMapping(value = "/list/paged")
    public Result<Pagination<CommonResourceVo>> getCurrentUserPagedReport(@ApiParam("查询条件") @RequestBody PagedQueryItem<Map> queryItem) {
        return reportPageService.getPagedReport(queryItem);
    }

    @GetMapping(value = "/detail")
    @ApiOperation("查询报表信息及权限")
    public Result<ReportPermissionVo> getReportDetail(@ApiParam("报表主键") @RequestParam("resId") String resId) {
        return reportPageService.getReportDetail(resId);
    }

}
