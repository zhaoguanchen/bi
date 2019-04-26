package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.vo.DatasourceVO;
import com.yiche.bigdata.service.DataManagerService;
import com.yiche.bigdata.service.DatasourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("star/admin/dataSource")
@Api(description = "数据源管理接口")
public class DatasourceController {

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private DataManagerService dataManagerService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation("获取所有数据")
    public Result<List<Datasource>> getDatasourceList(){
        return datasourceService.getDatasourceList();
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiOperation("根据主键查询一条数据源")
    public Result<Datasource> getDatasourceByResId(@ApiParam("数据源主键")@RequestParam String id){
        return datasourceService.getDatasourceByResId(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("新增数据源")
    public Result addDatasource(@ApiParam("数据源对应参数") @RequestBody DatasourceVO datasourceVO) {
        return datasourceService.addDatasource(datasourceVO);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation("修改数据源")
    public Result updateDatasource(@ApiParam("数据源参数") @RequestBody Datasource datasource) {
        return datasourceService.updateDatasource(datasource);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiOperation("删除数据源")
    public Result deleteDatasource(@ApiParam("数据源主键")@RequestParam String id) {
        return datasourceService.deleteDatasource(id);
    }

    @ApiOperation("测试数据源连接")
    @RequestMapping(value = "/test/connect", method = RequestMethod.GET)
    public Result testConnect(@RequestParam(name = "type") String type,
                       @RequestParam(name = "config") String config,
                       @RequestParam(name = "query") String query) {
        return dataManagerService.testConnect(type, config, query);
    }
}
