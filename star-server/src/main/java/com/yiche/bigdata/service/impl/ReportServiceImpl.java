package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.Constant;
import com.yiche.bigdata.constants.ReportType;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.ReportDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Report;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.ReportVO;
import com.yiche.bigdata.service.ReportService;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.EnumUtils;
import com.yiche.bigdata.utils.ResultUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportDao reportDao;

    @Autowired
    ResourceTreeService resourceTreeService;

    @Override
    public Result getReportByResId(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Report Report = reportDao.getReportByResId(resId);
        if (Report == null) {
            return ResultUtils.buildResult(ResultCode.REPORT_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, Report);
    }

    @Override
    public Result addReportDirectory(DirectoryVO directoryVO) {
        if (directoryVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!resourceTreeService.checkNodeNameOfBusiness(directoryVO.getName(), ResourceType.REPORT_DIRECTORY.value(), directoryVO.getBusinessLine())) {
            return ResultUtils.buildResult(ResultCode.DIRECTORY_NAME_REPEAT);
        }
        Map<String, Object> param = new HashMap<String, Object>(5);
        param.put("name", directoryVO.getName());
        param.put("pid", directoryVO.getPid());
        param.put("type", ResourceType.REPORT_DIRECTORY.value());
        param.put("description", directoryVO.getDescription());
        param.put("businessLine", directoryVO.getBusinessLine());
        return resourceTreeService.addNode(param);
    }

    @Override
    public Result updateReportDirectory(DirectoryVO directoryVO) {
        if (directoryVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }

        ResTree resTree = resourceTreeService.getNodeById(directoryVO.getResId());
        if (!resTree.getName().equals(directoryVO.getName())) {
            if (!resourceTreeService.checkNodeNameOfBusiness(directoryVO.getName(), ResourceType.REPORT_DIRECTORY.value(), directoryVO.getBusinessLine())) {
                return ResultUtils.buildResult(ResultCode.DIRECTORY_NAME_REPEAT);
            }
        }

        Map<String, Object> param = new HashMap<String, Object>(5);
        param.put("name", directoryVO.getName());
        param.put("pid", directoryVO.getPid());
        param.put("type", ResourceType.REPORT_DIRECTORY.value());
        param.put("description", directoryVO.getDescription());
        param.put("businessLine", directoryVO.getBusinessLine());
        return resourceTreeService.updateNode(directoryVO.getResId(), param);
    }


    /**
     * 删除文件夹
     *
     * @param resId
     * @return
     */
    @Override
    public Result deleteReportDirectory(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(resId);
        if (resTree.getName().equals(Constant.DEFAULT_DIRECTORY.getStringName())) {
            ResultUtils.buildResult(ResultCode.NOT_ALLOWED_TO_DELETE_DEFAULT_DIRECTORY);
        }
        return resourceTreeService.deleteNodeById(resId);
    }

    @Override
    public Result addReport(ReportVO reportVO) {
        if (reportVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!resourceTreeService.checkNodeNameOfBusiness(reportVO.getName(), ResourceType.REPORT.value(), reportVO.getBusinessLine())) {
            return ResultUtils.buildResult(ResultCode.REPORT_NAME_EXIST);
        }
        ResTree parentNode = resourceTreeService.getNodeTypeAndName(Constant.DEFAULT_DIRECTORY.getStringName(),
                ResourceType.REPORT_DIRECTORY.value(), reportVO.getBusinessLine());
        if (parentNode == null) {
            return ResultUtils.buildResult(ResultCode.DEFAULT_DIRECTORY_NOT_EXIST);
        }
        Report report = new Report();
        String resId = CommonUtils.getUUID();
        report.setResId(resId);
        report.setName(reportVO.getName());
        report.setDatasetId(reportVO.getDatasetId());
        report.setLayoutJson(reportVO.getLayoutJson());
        ReportType typeEnum = EnumUtils.valueOf(ReportType.class, reportVO.getType());
        report.setType(typeEnum.value());
        if (!reportDao.addDataTable(report)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        Map<String, Object> param = new HashMap<String, Object>(5);
        param.put("resId", report.getResId());
        param.put("name", reportVO.getName());
        param.put("pid", reportVO.getPid());
        param.put("type", ResourceType.REPORT.value());
        param.put("businessLine", reportVO.getBusinessLine());
        return resourceTreeService.addNode(param);
    }

    @Override
    public Result copyReport(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(resId);
        String copyResId = CommonUtils.getUUID();


        int i = 1;
        String copyName = resTree.getName() + "(" + i + ")";
        while (!resourceTreeService.checkNodeNameOfBusiness(copyName, ResourceType.REPORT.value(), resTree.getBusinesLine())) {
            i++;
            copyName = resTree.getName() + "(" + i + ")";
        }
        if (resTree.getType() == ResourceType.REPORT.value()) {
            Report report = reportDao.getReportByResId(resId);
            if (report == null) {
                return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
            }
            report.setResId(copyResId);
            report.setName(copyName);
            if (!reportDao.addDataTable(report)) {
                return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
            }
        }

        Map<String, Object> param = new HashMap<String, Object>(5);
        param.put("resId", copyResId);
        param.put("name", copyName);
        param.put("pid", resTree.getPid());
        param.put("type", ResourceType.REPORT.value());
        param.put("businessLine", resTree.getBusinesLine());
        return resourceTreeService.addNode(param);
    }

    @Override
    public Result updateReport(Report report) {
        if (report == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree node = resourceTreeService.getNodeById(report.getResId());
        if (node == null) {
            return ResultUtils.buildResult(ResultCode.NODE_NOT_EXIST);
        }
        if (!node.getName().equals(report.getName())) {
            if (!resourceTreeService.checkNodeNameOfBusiness(report.getName(), ResourceType.REPORT.value(), node.getBusinesLine())) {
                return ResultUtils.buildResult(ResultCode.REPORT_NAME_EXIST);
            }
        }
        if (node.getType() == ResourceType.REPORT.value()) {
            if (!reportDao.updateReport(report)) {
                return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
            }
        }


        Map<String, Object> param = new HashMap<String, Object>(1);
        param.put("name", report.getName());
        return resourceTreeService.updateNode(report.getResId(), param);
    }

    @Override
    public Result moveReport(String resId, String targetDirectoryId) {
        if (resId == null || targetDirectoryId == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(resId);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pid", targetDirectoryId);
        return resourceTreeService.updateNode(resId, param);

    }

    @Override
    public Result deleteReport(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree node = resourceTreeService.getNodeById(resId);
        if (node == null) {
            return ResultUtils.buildResult(ResultCode.NODE_NOT_EXIST);
        }

        if (!reportDao.deleteReport(resId)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }

        return resourceTreeService.deleteNodeById(resId);
    }
}
