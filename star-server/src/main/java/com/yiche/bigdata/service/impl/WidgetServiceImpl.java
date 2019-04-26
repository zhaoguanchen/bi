package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.Constant;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.ResTreeDao;
import com.yiche.bigdata.dao.WidgetDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.WidgetVO;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.service.WidgetService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.ResultUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WidgetServiceImpl implements WidgetService {

    @Autowired
    WidgetDao widgetDao;

    @Autowired
    ResTreeDao resTreeDao;

    @Autowired
    ResourceTreeService resourceTreeService;

    /**
     * 新增文件夹
     *
     * @param directoryVO
     * @return
     */
    @Override
    public Result addWidgetDirectory(DirectoryVO directoryVO) {
        if (directoryVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!resourceTreeService.checkNodeNameOfBusiness(directoryVO.getName(), ResourceType.WIDGET_DIRECTORY.value(), directoryVO.getBusinessLine())) {
            return ResultUtils.buildResult(ResultCode.DIRECTORY_NAME_REPEAT);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", directoryVO.getName());
        param.put("pid", directoryVO.getPid());
        param.put("type", ResourceType.WIDGET_DIRECTORY.value());
        param.put("description", directoryVO.getDescription());
        param.put("businessLine", directoryVO.getBusinessLine());
        return resourceTreeService.addNode(param);
    }

    /**
     * 更新文件夹
     *
     * @param directoryVO
     * @return
     */
    @Override
    public Result updateWidgetDirectory(DirectoryVO directoryVO) {
        if (directoryVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(directoryVO.getResId());
        if (!resTree.getName().equals(directoryVO.getName())) {
            if (!resourceTreeService.checkNodeNameOfBusiness(directoryVO.getName(), ResourceType.WIDGET_DIRECTORY.value(), directoryVO.getBusinessLine())) {
                return ResultUtils.buildResult(ResultCode.DIRECTORY_NAME_REPEAT);
            }
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", directoryVO.getName());
        param.put("pid", directoryVO.getPid());
        param.put("type", ResourceType.WIDGET_DIRECTORY.value());
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
    public Result deleteDirectory(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(resId);
        if (resTree.getName().equals(Constant.DEFAULT_DIRECTORY.getStringName())) {
            ResultUtils.buildResult(ResultCode.NOT_ALLOWED_TO_DELETE_DEFAULT_DIRECTORY);
        }
        return resourceTreeService.deleteNodeById(resId);
    }

    /**
     * 通过ID获取一条单图
     *
     * @param resId
     * @return
     */

    @Override
    public Result getWidgetByResId(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Widget Widget = widgetDao.getWidgetByResId(resId);
        if (Widget == null) {
            return ResultUtils.buildResult(ResultCode.WIDGET_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, Widget);
    }

    /**
     * 新增图表
     *
     * @param widgetVO
     * @return
     */
    @Override
    public Result addWidget(WidgetVO widgetVO) {
        if (widgetVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!resourceTreeService.checkNodeNameOfBusiness(widgetVO.getName(), ResourceType.WIDGET.value(), widgetVO.getBusinessLine())) {
            return ResultUtils.buildResult(ResultCode.WIDGET_NAME_EXIST);
        }

        ResTree parentNode = resourceTreeService.getNodeTypeAndName(Constant.DEFAULT_DIRECTORY.getStringName(),
                ResourceType.WIDGET_DIRECTORY.value(), widgetVO.getBusinessLine());
        if (parentNode == null) {
            return ResultUtils.buildResult(ResultCode.DEFAULT_DIRECTORY_NOT_EXIST);
        }
        Widget widget = new Widget();
        String resId = CommonUtils.getUUID();
        widget.setResId(resId);
        widget.setName(widgetVO.getName());
        widget.setDataJson(widgetVO.getDataJson());
        widget.setDatasetId(widgetVO.getDatasetId());
        widget.setChartType(widgetVO.getChartType());
        if (!widgetDao.addWidget(widget)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("resId", widget.getResId());
        param.put("name", widgetVO.getName());
        param.put("pid", parentNode.getId());
        param.put("type", ResourceType.WIDGET.value());
        param.put("businessLine", widgetVO.getBusinessLine());
        return resourceTreeService.addNode(param);
    }

    /**
     * 复制图表
     *
     * @param resId
     * @return
     */
    @Override
    public Result copyWidget(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Widget widget = widgetDao.getWidgetByResId(resId);
        if (widget == null) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        widget.setResId(CommonUtils.getUUID());
        if (!widgetDao.addWidget(widget)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        ResTree resTree = resourceTreeService.getNodeById(resId);

        int i = 1;
        String widgetName = widget.getName() + "(" + i + ")";
        while (!resourceTreeService.checkNodeNameOfBusiness(widgetName, ResourceType.WIDGET.value(), resTree.getBusinesLine())) {
            i++;
            widgetName = widget.getName() + "(" + i + ")";
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("resId", widget.getResId());
        param.put("name", widgetName);
        param.put("pid", resTree.getPid());
        param.put("type", ResourceType.WIDGET.value());
        param.put("businessLine", resTree.getBusinesLine());
        return resourceTreeService.addNode(param);
    }

    /**
     * 更新图表
     *
     * @param widget
     * @return
     */
    @Override
    public Result updateWidget(Widget widget) {
        if (widget == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(widget.getResId());
        if (!resTree.getName().equals(widget.getName())) {
            if (!resourceTreeService.checkNodeNameOfBusiness(widget.getName(), ResourceType.WIDGET.value(), resTree.getBusinesLine())) {
                return ResultUtils.buildResult(ResultCode.WIDGET_NAME_EXIST);
            }
        }

        if (!widgetDao.updateWidget(widget)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", widget.getName());
        return resourceTreeService.updateNode(widget.getResId(), param);
    }

    /**
     * 移动图表
     *
     * @param resId
     * @param targetDirectoryId
     * @return
     */
    @Override
    public Result moveWidget(String resId, String targetDirectoryId) {
        if (resId == null || targetDirectoryId == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(resId);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pid", targetDirectoryId);
        return resourceTreeService.updateNode(resId, param);

    }

    /**
     * 删除图表
     *
     * @param resId
     * @return
     */
    @Override
    public Result deleteWidget(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }

// TODO: 2019/4/17  4.图表若无关联的业务报告、业务大盘，点击删除按钮弹窗提示：“确定要删除XXX图表吗？”；图表若有关联的业务报告、业务大盘，点击删除按钮弹窗提示：“XXX业务报告、XXX业务大盘已使用该图表，请先于业务报告业务大盘中删除”。

        if (!widgetDao.deleteWidget(resId)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        return resourceTreeService.deleteNodeById(resId);
    }


}
