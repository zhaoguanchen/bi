package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.ReportDao;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Report;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.vo.*;
import com.yiche.bigdata.service.ReportPageService;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.service.core.*;
import com.yiche.bigdata.utils.ResultUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReportPageServiceImpl extends AbstractUserResourceService implements ReportPageService {

    @Autowired
    private ResourceTreeService resourceTreeService;

    @Autowired
    private UserComponentService userComponentService;

    @Autowired
    private ReportDao reportDao;

    @Override
    public Result getReportCommonPermission(String businessLine) {
        return ResultUtils.buildResult(ResultCode.OK
                , userComponentService.getBusinessLineLevelPermission(businessLine, ResourceType.REPORT));
    }

    /**
     * 按文件夹获取报告列表
     *
     * @param businessLine
     * @param search
     * @return
     */
    @Override
    public Result getReportList(String businessLine, String search) {
        List<ReportListVO> result = new ArrayList<>();

        if (search == "") {

            List<ReportListVO> directoryList = getAllDirectory(businessLine, search);
            if (directoryList != null) {

                for (ReportListVO directoryItem : directoryList) {

                    List<ReportListVO> widgetList = getReportListByDir(directoryItem.getId(), search);

                    directoryItem.setChildren(widgetList);
                }
                result = directoryList;
            }
        } else {
            List<ReportListVO> directoryList = getAllDirectory(businessLine, "");
            if (directoryList != null) {
                for (ReportListVO directoryItem : directoryList) {
                    if (directoryItem.getName().contains(search)) {
                        List<ReportListVO> widgetList = getReportListByDir(directoryItem.getId(), "");
                        directoryItem.setChildren(widgetList);
                        result.add(directoryItem);
                    } else {
                        List<ReportListVO> widgetList = getReportListByDir(directoryItem.getId(), search);
                        if (!widgetList.isEmpty()) {
                            directoryItem.setChildren(widgetList);
                            result.add(directoryItem);
                        }
                    }
                }
            }

        }

        return ResultUtils.buildResult(ResultCode.OK, result);
    }

    /**
     * 所有文件夹列表
     *
     * @param pid
     * @param search
     */
    private List<ReportListVO> getAllDirectory(String pid, String search) {
        List<ReportListVO> result = new ArrayList<>();
        if (pid == null) {
            return null;
        }
        Result<List<CommonResourceVo>> children = getResourceList(pid, search);

        if (children.getResult() != null && children.getResult().size() > 0) {
            children.getResult().stream().forEach((child) -> {
                if (child.getType().equals(ResourceType.REPORT_DIRECTORY)) {
                    if (CollectionUtils.isEmpty(result)) {
                        ReportListVO widgetListVO = new ReportListVO(child);
                        result.add(widgetListVO);
                    } else {
                        //按创建时间降序排列
                        boolean addFlag = false;
                        for (int i = 0; i < result.size(); i++) {
                            if (child.getCreateTime() != null && result.get(i).getCreateTime() != null) {
                                if (child.getCreateTime().after(result.get(i).getCreateTime())) {
                                    ReportListVO reportListVO = new ReportListVO(child);
                                    result.add(reportListVO);
                                    addFlag = true;
                                    break;
                                }
                            }
                        }
                        if (!addFlag) {
                            ReportListVO reportListVO = new ReportListVO(child);
                            result.add(reportListVO);
                        }
                    }
                }
            });
        }
        return result;
    }

    /**
     * 文件夹下单图列表
     *
     * @param pid
     * @param search
     */

    private List<ReportListVO> getReportListByDir(String pid, String search) {
        List<ReportListVO> result = new ArrayList<>();
        if (pid == null) {
            return null;
        }
        Result<List<CommonResourceVo>> children = getResourceList(pid, search);
        if (children.getResult() != null && children.getResult().size() > 0) {
            children.getResult().stream().forEach((child) -> {
                if (child.getType().equals(ResourceType.REPORT)) {
                    if (CollectionUtils.isEmpty(result)) {
                        ReportListVO reportListVO = new ReportListVO(child);
                        result.add(reportListVO);
                    } else {
                        //按创建时间降序排列
                        boolean addFlag = false;
                        for (int i = 0; i < result.size(); i++) {
                            if (child.getLastModifyTime() != null && result.get(i).getLastModifyTime() != null) {
                                if (child.getLastModifyTime().after(result.get(i).getLastModifyTime())) {
                                    ReportListVO reportListVO = new ReportListVO(child);
                                    result.add(reportListVO);
                                    addFlag = true;
                                    break;
                                }
                            }
                        }
                        if (!addFlag) {
                            ReportListVO reportListVO = new ReportListVO(child);
                            result.add(reportListVO);
                        }
                    }
                }
            });
        }
        return result;
    }


    @Override
    public Result getPagedReport(PagedQueryItem<Map> queryItem) {
        return getPagedResource(queryItem);
    }

    @Override
    public Result getReportDetail(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.NODE_NOT_EXIST);
        }
        UserContext userContext = getUserContext();
        BaseNode node = userContext.getResourceNode(resId);
        ResTree resNode = resourceTreeService.getNodeById(resId);
        ReportPermissionVo reportPermissionVo = new ReportPermissionVo();
        Map<String, String> componentPermissions = userComponentService.getComponentOptPermission(node, resNode.getBusinesLine());
        reportPermissionVo.setPermissions(componentPermissions);
        Report report = reportDao.getReportByResId(resId);
        if (report == null) {
            ResultUtils.buildResult(ResultCode.REPORT_NOT_EXIST);
        }
        ComponentPermissionVo componentPermissionVo = new ComponentPermissionVo();
        BeanUtils.copyProperties(report, componentPermissionVo);
        componentPermissionVo.setPermissions(componentPermissions);
        List<ComponentPermissionVo> widgetsPermissionVoList =
                userComponentService.getWidgetsPermission(report.getLayoutJson(), resNode.getBusinesLine());
        reportPermissionVo.setWidgetsPermission(widgetsPermissionVoList);
        return ResultUtils.buildResult(ResultCode.OK, reportPermissionVo);
    }

    @Override
    protected void resourceVoPostProcessing(List<CommonResourceVo> voList) {
        for (CommonResourceVo commonResourceVo : voList) {
            if (ResourceType.REPORT_DIRECTORY.equals(commonResourceVo.getType())) {
                commonResourceVo.getPermissions().remove("copy");
            }
            commonResourceVo.getPermissions().remove("add_report");
        }
    }

    @Override
    ResourceType[] getResourceType() {
        return new ResourceType[]{ResourceType.REPORT_DIRECTORY, ResourceType.REPORT};
    }
}
