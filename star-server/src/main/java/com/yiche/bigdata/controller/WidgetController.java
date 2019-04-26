package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.WidgetVO;
import com.yiche.bigdata.service.WidgetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("star/admin/widget")
@Api(description = "图表数据管理接口")
public class WidgetController {

    @Autowired
    WidgetService widgetService;


    @RequestMapping(value = "/addDirectory", method = RequestMethod.POST)
    @ApiOperation("新增图表目录，同时新增资源树")
    public Result addDirectory(@ApiParam("图表目录的参数") @RequestBody DirectoryVO directoryVO) {
        return widgetService.addWidgetDirectory(directoryVO);
    }

    @RequestMapping(value = "/updateDirectory", method = RequestMethod.POST)
    @ApiOperation("更新图表目录，同时更新资源树")
    public Result updateDirectory(@ApiParam("图表目录的参数") @RequestBody DirectoryVO directoryVO) {
        return widgetService.updateWidgetDirectory(directoryVO);
    }


    @RequestMapping(value = "/deleteDirectory", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string")
    @ApiOperation("删除图表目录，同时删除资源树")
    public Result deleteDirectory(@ApiParam("文件夹的主键")@RequestBody Map<String, Object> reqMap) {
        return widgetService.deleteDirectory((String) reqMap.get("resId"));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("新增图表，同时新增资源树")
    public Result addWidget(@ApiParam("图标的参数") @RequestBody WidgetVO widgetVO) {
        return widgetService.addWidget(widgetVO);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string")
    @ApiOperation("复制图表，同时新增资源树")
    public Result copyWidget(@ApiParam("图表主键") @RequestBody Map<String, Object> reqMap) {
        return widgetService.copyWidget((String) reqMap.get("resId"));
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string, targetDirectoryId:string")
    @ApiOperation("移动图表，同时修改资源树")
    public Result moveWidget(@ApiParam("图表主键") @RequestBody Map<String, Object> reqMap) {
        return widgetService.moveWidget((String) reqMap.get("resId"), (String) reqMap.get("targetDirectoryId"));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation("修改图表，同时修改资源树")
    public Result updateWidget(@ApiParam("图表的参数") @RequestBody Widget widget) {
        return widgetService.updateWidget(widget);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiImplicitParam(name = "reqMap", paramType = "body", dataType = "json", required = true
            , value = "resId :string")
    @ApiOperation("删除图表，同时删除资源树")
    public Result deleteWidget(@ApiParam("图表的主键") @RequestBody Map<String, Object> reqMap) {
        return widgetService.deleteWidget((String) reqMap.get("resId"));
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiOperation("根据主键查询一条图表数据")
    public Result<Widget> getWidgetListByResIds(@ApiParam("图表的主键") @RequestParam String resId) {
        return widgetService.getWidgetByResId(resId);
    }

}
