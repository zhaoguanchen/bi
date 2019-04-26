package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.Constant;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.*;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.vo.DatasetVO;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.ExpressionValidateVO;
import com.yiche.bigdata.service.DataSetPageService;
import com.yiche.bigdata.service.DatasetService;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.ExpressionUtils;
import com.yiche.bigdata.utils.ResultUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DatasetServiceImpl implements DatasetService {

    private static final String EMPTYDATABASE = "empty_database";

    @Autowired
    DatasetDao datasetDao;

    @Autowired
    MetaDataDao metaDataDao;

    @Autowired
    WidgetDao widgetDao;

    @Autowired
    ReportDao reportDao;

    @Autowired
    ResTreeDao resTreeDao;

    @Autowired
    ResourceTreeService resourceTreeService;

    @Autowired
    DatasetService datasetService;

    @Autowired
    DataSetPageService dataSetPageService;


    @Override
    public Result<Dataset> getDatasetByResId(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Dataset dataset = datasetDao.getDatasetByResId(resId);
        if (dataset == null) {
            return ResultUtils.buildResult(ResultCode.DATASET_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, dataset);
    }

    @Override
    public Result addDataSetDirectory(DirectoryVO directoryVO) {
        if (directoryVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!resourceTreeService.checkNodeNameOfBusiness(directoryVO.getName(), ResourceType.DATA_SET_DIRECTORY.value(), directoryVO.getBusinessLine())) {
            return ResultUtils.buildResult(ResultCode.DIRECTORY_NAME_REPEAT);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", directoryVO.getName());
        param.put("pid", directoryVO.getPid());
        param.put("type", ResourceType.DATA_SET_DIRECTORY.value());
        param.put("description", directoryVO.getDescription());
        param.put("businessLine", directoryVO.getBusinessLine());
        return resourceTreeService.addNode(param);
    }

    @Override
    public Result updateDataSetDirectory(DirectoryVO directoryVO) {

        if (directoryVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(directoryVO.getResId());
        if (!resTree.getName().equals(directoryVO.getName())) {
            if (!resourceTreeService.checkNodeNameOfBusiness(directoryVO.getName(), ResourceType.DATA_SET_DIRECTORY.value(), directoryVO.getBusinessLine())) {
                return ResultUtils.buildResult(ResultCode.DIRECTORY_NAME_REPEAT);
            }
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", directoryVO.getName());
        return resourceTreeService.updateNode(directoryVO.getResId(), param);


    }

    @Override
    public Result addDataset(DatasetVO datasetVO) {
        if (datasetVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!resourceTreeService.checkNodeNameOfBusiness(datasetVO.getName(), ResourceType.DATA_SET.value(), datasetVO.getBusinessLine())) {
            return ResultUtils.buildResult(ResultCode.DATA_SET_NAME_EXIST);
        }
        ResTree parentNode = resourceTreeService.getNodeTypeAndName(Constant.DEFAULT_DIRECTORY.getStringName(),
                ResourceType.DATA_SET_DIRECTORY.value(), datasetVO.getBusinessLine());
        if (parentNode == null) {
            return ResultUtils.buildResult(ResultCode.DEFAULT_DIRECTORY_NOT_EXIST);
        }

        Dataset dataset = new Dataset();
        String resId = CommonUtils.getUUID();
        dataset.setResId(resId);
        dataset.setName(datasetVO.getName());
        dataset.setDataJson(datasetVO.getDataJson());
        dataset.setTimePrimaryKey(datasetVO.getTimePrimaryKey());
        if (!datasetDao.addDataTable(dataset)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("resId", dataset.getResId());
        param.put("name", datasetVO.getName());
        param.put("pid", datasetVO.getPid());
        param.put("type", ResourceType.DATA_SET.value());
        param.put("businessLine", datasetVO.getBusinessLine());
        return resourceTreeService.addNode(param);
    }

    /*
        @Override
        public Result copyDataset(String resId) {
            if (StringUtils.isEmpty(resId)) {
                return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
            }
            Dataset dataset = datasetDao.getDatasetByResId(resId);
            if (dataset == null) {
                return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
            }
            dataset.setResId(CommonUtils.getUUID());
            if (!datasetDao.addDataTable(dataset)) {
                return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
            }
            ResTree resTree = resourceTreeService.getNodeById(resId);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("resId", dataset.getResId());
            param.put("name", dataset.getName());
            param.put("pid", resTree.getPid());
            param.put("type", ResourceType.DATA_SET.value());
            param.put("businessLine", resTree.getBusinesLine());
            return resourceTreeService.addNode(param);
        }
    */
    @Override
    public Result updateDataset(Dataset dataset) {
        if (dataset == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(dataset.getResId());
        if (!resTree.getName().equals(dataset.getName())) {
            if (!resourceTreeService.checkNodeNameOfBusiness(dataset.getName(), ResourceType.DATA_SET.value(), resTree.getBusinesLine())) {
                return ResultUtils.buildResult(ResultCode.DATA_SET_NAME_EXIST);
            }
        }

        if (!datasetDao.updateDataset(dataset)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", dataset.getName());
        return resourceTreeService.updateNode(dataset.getResId(), param);
    }

    @Override
    public Result deleteDataset(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }

        if (widgetDao.countByDateSetId(resId)) {
            return ResultUtils.buildResult(ResultCode.DATA_SET_USED_BY_WIDGET);
        }

        if (reportDao.countByDateSetId(resId)) {
            return ResultUtils.buildResult(ResultCode.DATA_SET_USED_BY_REPORT);
        }

        if (!datasetDao.deleteDataset(resId)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        return resourceTreeService.deleteNodeById(resId);
    }


}
