package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.constants.RoleType;
import com.yiche.bigdata.dao.WidgetDao;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.vo.CommonResourceVo;
import com.yiche.bigdata.entity.vo.ResourceSelectorVo;
import com.yiche.bigdata.entity.vo.WidgetListVO;
import com.yiche.bigdata.service.WidgetPageService;
import com.yiche.bigdata.service.core.BaseNode;
import com.yiche.bigdata.service.core.ResTreeNode;
import com.yiche.bigdata.service.core.UserComponentService;
import com.yiche.bigdata.service.core.UserContext;
import com.yiche.bigdata.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WidgetPageServiceImpl extends AbstractUserResourceService implements WidgetPageService {

    @Autowired
    private UserComponentService userComponentService;

    @Autowired
    private WidgetDao widgetDao;

    @Override
    public Result getWidgetCommonPermission(String businessLine) {
        return ResultUtils.buildResult(ResultCode.OK
                , userComponentService.getBusinessLineLevelPermission(businessLine, ResourceType.WIDGET));
    }

    @Override
    public Result getWidgetSelector(String pid, String filter) {
        List<ResourceSelectorVo> selectorVoList = userComponentService.listUserResourceSelectorVo(pid, ResourceType.WIDGET.value(), filter);
        return ResultUtils.buildResult(ResultCode.OK, selectorVoList);
    }

    /*
        @Override
        public Result getWidgetList(String businessLine, String search) {
            List<CommonResourceVo> result = new ArrayList<>();
            getAllWidget(result, businessLine, search);
            List<CommonResourceVo> sortedResult = new ArrayList<>();
            result.stream().forEach((vo) -> {
                if(CollectionUtils.isEmpty(sortedResult)){
                    sortedResult.add(vo);
                }else{
                    //按修改时间降序排列
                    boolean addflag = false;
                    for (int i = 0; i < sortedResult.size(); i++) {
                        if(vo.getCreateTime() != null && sortedResult.get(i).getLastModifyTime()!= null){
                            if(vo.getCreateTime().after(sortedResult.get(i).getLastModifyTime())){
                                sortedResult.add(i, vo);
                                addflag = true;
                                break;
                            }
                        }
                    }
                    if(!addflag){
                        sortedResult.add(vo);
                    }
                }
            });
            return ResultUtils.buildResult(ResultCode.OK, sortedResult);
        }
    */

    /**
     * 按文件夹获取单图列表
     *
     * @param businessLine
     * @param search
     * @return
     */
    @Override
    public Result getWidgetList(String businessLine, String search) {
        List<WidgetListVO> result = new ArrayList<>();

        if (search == "") {

            List<WidgetListVO> directoryList = getAllWidgetDirectory(businessLine, search);
            if (directoryList != null) {

                for (WidgetListVO directoryItem : directoryList) {

                    List<WidgetListVO> widgetList = getWidgetListByDir(directoryItem.getId(), search);

                    directoryItem.setChildren(widgetList);
                }
                result = directoryList;
            }
        } else {
            List<WidgetListVO> directoryList = getAllWidgetDirectory(businessLine, "");
            if (directoryList != null) {
                for (WidgetListVO directoryItem : directoryList) {
                    if (directoryItem.getName().contains(search)) {
                        List<WidgetListVO> widgetList = getWidgetListByDir(directoryItem.getId(), "");
                        directoryItem.setChildren(widgetList);
                        result.add(directoryItem);
                    } else {
                        List<WidgetListVO> widgetList = getWidgetListByDir(directoryItem.getId(), search);
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

    private void getAllWidget(List<CommonResourceVo> result, String pid, String search) {
        if (pid == null) {
            return;
        }
        Result<List<CommonResourceVo>> children = getResourceList(pid, search);
        if (children.getResult() != null && children.getResult().size() > 0) {
            for (CommonResourceVo child : children.getResult()) {
                if (child.getType().equals(ResourceType.WIDGET)) {
                    result.add(child);
                } else if (child.getType().equals(ResourceType.WIDGET_DIRECTORY)) {
                    getAllWidget(result, child.getId(), search);
                }
            }
        }
    }

    /**
     * 所有文件夹列表
     *
     * @param pid
     * @param search
     */
    private List<WidgetListVO> getAllWidgetDirectory(String pid, String search) {
        List<WidgetListVO> result = new ArrayList<>();
        if (pid == null) {
            return null;
        }
        Result<List<CommonResourceVo>> children = getResourceList(pid, search);

        if (children.getResult() != null && children.getResult().size() > 0) {
            children.getResult().stream().forEach((child) -> {
                if (child.getType().equals(ResourceType.WIDGET_DIRECTORY)) {
                    if (CollectionUtils.isEmpty(result)) {
                        WidgetListVO widgetListVO = new WidgetListVO(child);
                        result.add(widgetListVO);
                    } else {
                        //按创建时间降序排列
                        boolean addFlag = false;
                        for (int i = 0; i < result.size(); i++) {
                            if (child.getCreateTime() != null && result.get(i).getCreateTime() != null) {
                                if (child.getCreateTime().after(result.get(i).getCreateTime())) {
                                    WidgetListVO widgetListVO = new WidgetListVO(child);
                                    result.add(widgetListVO);
                                    addFlag = true;
                                    break;
                                }
                            }
                        }
                        if (!addFlag) {
                            WidgetListVO widgetListVO = new WidgetListVO(child);
                            result.add(widgetListVO);
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

    private List<WidgetListVO> getWidgetListByDir(String pid, String search) {
        List<WidgetListVO> result = new ArrayList<>();
        if (pid == null) {
            return null;
        }
        Result<List<CommonResourceVo>> children = getResourceList(pid, search);
        if (children.getResult() != null && children.getResult().size() > 0) {
            children.getResult().stream().forEach((child) -> {
                if (child.getType().equals(ResourceType.WIDGET)) {
                    if (CollectionUtils.isEmpty(result)) {
                        WidgetListVO widgetListVO = new WidgetListVO(child);
                        result.add(widgetListVO);
                    } else {
                        //按创建时间降序排列
                        boolean addFlag = false;
                        for (int i = 0; i < result.size(); i++) {
                            if (child.getLastModifyTime() != null && result.get(i).getLastModifyTime() != null) {
                                if (child.getLastModifyTime().after(result.get(i).getLastModifyTime())) {
                                    WidgetListVO widgetListVO = new WidgetListVO(child);
                                    result.add(widgetListVO);
                                    addFlag = true;
                                    break;
                                }
                            }
                        }
                        if (!addFlag) {
                            WidgetListVO widgetListVO = new WidgetListVO(child);
                            result.add(widgetListVO);
                        }
                    }
                }
            });
        }
        return result;
    }

    @Override
    protected Result getResourceList(String businessLine, String search) {
        UserContext userContext = getUserContext();
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        BaseNode parentNode = userContext.getResourceNode(businessLine);
        if (parentNode == null) {
            return ResultUtils.buildResult(ResultCode.NO_PERMISSION);
        }
        RoleType roleType = userContext.getMaxRole(businessLine);
        List<BaseNode> widgetList = new ArrayList<>();
        collectWidget(widgetList, parentNode);

        List<ResTree> fixedNodeList = getFixedNodes(parentNode.getChildrenNodes(), businessLine, userContext);

        List<CommonResourceVo> personalResVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(fixedNodeList)) {
            for (ResTree node : fixedNodeList) {
                conditionFilter(search, roleType, personalResVoList, node);
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, personalResVoList);
    }

    @Override
    protected List<ResTree> getNewCreateTreeNodes(List<BaseNode> cachedNodes, String businessLine, UserContext
            userContext) {
        List<String> cachedResIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cachedNodes)) {
            for (BaseNode node : cachedNodes) {
                cachedResIds.add(node.getId());
            }
        }
        return resourceTreeService.listAllBusinessLineResByCreator(userContext.getUserId(), ResourceType.WIDGET.value(), businessLine, cachedResIds);
    }

    @Override
    protected void resourceVoPostProcessing(List<CommonResourceVo> voList) {
        if (!CollectionUtils.isEmpty(voList)) {
            UserContext userContext = getUserContext();
            String businessLine = ((ResTreeNode) userContext.getResTree().getNodeMapping()
                    .get(voList.get(0).getId())).getBusinesLine();
            RoleType roleType = userContext.getMaxRole(businessLine);
            Map<String, Map<String, String>> dataSetPermissionsCache = new HashMap<>();
            for (CommonResourceVo vo : voList) {
                Widget widget = widgetDao.getWidgetByResId(vo.getId());
                if (widget != null) {
                    Map<String, String> dataSetPermissions = userComponentService
                            .getWidgetPermissionFromDataSet(userContext, roleType, dataSetPermissionsCache, widget.getDatasetId());
                    vo.getPermissions().putAll(dataSetPermissions);
                    vo.getPermissions().remove("add_widget");
                }
            }
        }

    }

    private void collectWidget(List<BaseNode> widgetList, BaseNode parentNode) {
        List<BaseNode> children = parentNode.getChildrenNodes();
        if (!CollectionUtils.isEmpty(children)) {
            for (BaseNode node : children) {
                if (CollectionUtils.arrayToList(getResourceType()).contains(node.getType())) {
                    if (node.getType().equals(ResourceType.WIDGET)) {
                        widgetList.add(node);
                    } else if (node.getType().equals(ResourceType.WIDGET_DIRECTORY)) {
                        collectWidget(widgetList, node);
                    }
                }
            }
        }
    }

    @Override
    public Result getPagedWidget(PagedQueryItem<Map> queryItem) {
        String search = (String) queryItem.getCondition().get("search");
        String businessLine = (String) queryItem.getCondition().get("businessLine");
        Result allResult = this.getResourceList(businessLine, search);
        return getPagedResult(queryItem, allResult);
    }

    @Override
    ResourceType[] getResourceType() {
        return new ResourceType[]{ResourceType.WIDGET_DIRECTORY, ResourceType.WIDGET};
    }
}
