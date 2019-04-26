package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.vo.BusinessLineVo;
import com.yiche.bigdata.entity.vo.ReportPermissionVo;
import com.yiche.bigdata.entity.vo.MenuVo;
import com.yiche.bigdata.service.PortalPageService;
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
@RequestMapping("star/portal")
@Api(description = "BI主页接口")
public class PortalPageController {

    @Autowired
    private PortalPageService portalPageService;

    @GetMapping(value = "/userinfo")
    @ApiOperation("查询当前登录用户信息")
    public Result<List<MenuVo>> getCurrentUserInfo() {
        return portalPageService.getCurrentUserInfo();
    }

    @GetMapping(value = "/business_line/list")
    @ApiOperation("查询当前登录用户可用业务线列表")
    public Result<List<BusinessLineVo>> listUserAvailableBusinessLine() {
        return portalPageService.listUserAvailableBusinessLine();
    }

    @GetMapping(value = "/menu/list")
    @ApiOperation("查询当前登录用户选择的业务线可用菜单列表")
    public Result<List<MenuVo>> listUserAvailableMenu(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return portalPageService.listUserAvailableMenu(businessLine);
    }

    @GetMapping(value = "/dashboard")
    @ApiOperation("查询大盘信息")
    public Result<ReportPermissionVo> getDashboard(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return portalPageService.getDashboard(businessLine);
    }

}
