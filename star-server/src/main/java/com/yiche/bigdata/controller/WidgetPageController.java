package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.vo.CommonResourceVo;
import com.yiche.bigdata.entity.vo.ResourceSelectorVo;
import com.yiche.bigdata.entity.vo.WidgetListVO;
import com.yiche.bigdata.service.WidgetPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("star/widget")
@Api(description = "单表页面接口")
public class WidgetPageController {

    @Autowired
    private WidgetPageService widgetPageService;

    @GetMapping(value = "/common/permission")
    @ApiOperation("查询图表添加等权限")
    public Result<Map<String, String>> getReportCommonPermission(@ApiParam("业务线主键") @RequestParam("businessLine") String businessLine) {
        return widgetPageService.getWidgetCommonPermission(businessLine);
    }

/*
    @ApiOperation(value = "分页查询用户图表列表", notes = "查询条件 condition { search:string, businessLine:string}")
    @PostMapping(value = "/list/paged")
    public Result<Pagination<CommonResourceVo>> getCurrentUserPagedWidget(@ApiParam("查询条件") @RequestBody PagedQueryItem<Map> queryItem) {
        return widgetPageService.getPagedWidget(queryItem);
    }
*/
    @PostMapping(value = "/list")
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "businessLine :string, search:string")
    @ApiOperation("查询当前用户图表列表")
    public Result<List<WidgetListVO>> getCurrentUserWidget(@ApiParam("业务线主键和查询条件") @RequestBody Map<String, Object> reqMap) {
        return widgetPageService.getWidgetList((String) reqMap.get("businessLine")
                , (String) reqMap.get("search"));
    }

    @GetMapping(value = "/selector")
    @ApiOperation("查询当前用户图表下拉菜单")
    public Result<List<ResourceSelectorVo>> getWidgetSelector(@ApiParam("父资源Id") @RequestParam("pid") String pid) {
        return widgetPageService.getWidgetSelector(pid, null);
    }

    @GetMapping(value = "/selector/filtered")
    @ApiOperation("查询当前用户图表列表下拉菜单（数据集过滤后）")
    public Result<List<ResourceSelectorVo>> getWidgetSelector(@ApiParam("父资源Id") @RequestParam("pid") String pid,
                                                              @ApiParam("数据集Id") @RequestParam("dataSetId") String dataSetId) {
        return widgetPageService.getWidgetSelector(pid, dataSetId);
    }

}
