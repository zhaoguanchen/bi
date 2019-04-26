package com.yiche.bigdata.controller;

import com.yiche.bigdata.annotation.SystemLog;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dashboard;
import com.yiche.bigdata.entity.vo.DashboardVO;
import com.yiche.bigdata.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/star/admin/dashboard")
@Api(description = "大盘数据管理接口")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiOperation("根据主键查询一条大盘数据")
    public Result<Dashboard> getDashboardByResId(@ApiParam("大盘主键") @RequestParam("resId") String resId) {
        return dashboardService.getDashboardByResId(resId);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("新增大盘数据，同时添加到对应资源树")
    @SystemLog("新增大盘数据，同时添加到对应资源树")
    public Result addDashboard(@ApiParam("大盘的参数+业务线+父节点ID") @Valid @RequestBody DashboardVO dashboardVO) {
        return dashboardService.addDashboard(dashboardVO);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation("修改大盘数据，同时修改对应资源树")
    @SystemLog("修改大盘数据，同时修改对应资源树")
    public Result updateDashboard(@ApiParam("大盘的参数") @RequestBody Dashboard dashboard) {
        return dashboardService.updateDashboard(dashboard);
    }

}
