package com.yiche.bigdata.controller;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.vo.DatasetVO;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.ExpressionValidateVO;
import com.yiche.bigdata.entity.vo.PairData;
import com.yiche.bigdata.service.DatasetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("star/admin/dataSet")
@Api(description = "数据集、数据集目录管理接口")
public class DatasetController {

    @Autowired
    DatasetService datasetService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiOperation("根据主键查询一条数据集")
    public Result<Dataset> getDataSetByResId(@ApiParam("数据集主键") @RequestParam String resId) {
        return datasetService.getDatasetByResId(resId);
    }

    @RequestMapping(value = "/addDirectory", method = RequestMethod.POST)
    @ApiOperation("新增数据集目录，同时新增资源树")
    public Result addDataSetDirectory(@ApiParam("数据集目录参数") @RequestBody DirectoryVO directoryVO) {
        return datasetService.addDataSetDirectory(directoryVO);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("新增数据集，同时新增资源树")
    public Result addDataSet(@ApiParam("数据集的参数") @RequestBody DatasetVO datasetVO) {
        return datasetService.addDataset(datasetVO);
    }

    /*暂时不用
        @RequestMapping(value = "/copy", method = RequestMethod.POST)
        @ApiOperation("复制数据集，同时新增资源树")
        public Result copyDataset(@ApiParam("数据集主键") @RequestParam String resId) {
            return datasetService.copyDataset(resId);
        }
    */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation("修改数据集，同时修改资源树")
    public Result updateDataSet(@ApiParam("数据集参数") @RequestBody Dataset dataset) {
        return datasetService.updateDataset(dataset);
    }

    @RequestMapping(value = "/updateDirectory", method = RequestMethod.POST)
    @ApiOperation("修改数据集目录，同时修改资源树")
    public Result updateDataSetDirectory(@ApiParam("数据集目录参数") @RequestBody DirectoryVO directoryVO) {
        return datasetService.updateDataSetDirectory(directoryVO);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiOperation("删除数据集，同时删除资源树")
    public Result deleteDataSet(@ApiParam("数据集主键") @RequestParam String resId) {
        return datasetService.deleteDataset(resId);
    }

}
