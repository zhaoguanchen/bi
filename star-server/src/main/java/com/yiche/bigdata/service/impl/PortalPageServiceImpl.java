package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.constants.RoleType;
import com.yiche.bigdata.dao.DashboardDao;
import com.yiche.bigdata.dao.OperationResourceDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dashboard;
import com.yiche.bigdata.entity.generated.OperationResource;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.vo.*;
import com.yiche.bigdata.service.PortalPageService;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.service.core.*;
import com.yiche.bigdata.utils.ResultUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PortalPageServiceImpl implements PortalPageService {

    @Autowired
    private UserContextContainer userContextContainer;

    @Autowired
    private UserComponentService userComponentService;

    @Autowired
    private OperationResourceDao operationResourceDao;

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private ResourceTreeService resourceTreeService;

    @Override
    public Result getCurrentUserInfo() {
        UserContext userContext = getUserContext();
        return ResultUtils.buildResult(ResultCode.OK, new DomainUserInfoVo(userContext.getUserInfo()));
    }

    @Override
    public Result listUserAvailableBusinessLine() {
        UserContext userContext = getUserContext();
        List<BaseNode> nodeList = userContext.getResTree().getRootNode().getChildrenNodes();
        if(CollectionUtils.isEmpty(nodeList)){
            return ResultUtils.buildResult(ResultCode.NO_AVAILABLE_BUSINESS_LINE);
        }
        List<BusinessLineVo> availableVoList = new ArrayList<>();
        for (BaseNode node : nodeList) {
            BusinessLineVo businessLineVo = new BusinessLineVo();
            BeanUtils.copyProperties(node, businessLineVo);
            businessLineVo.setSort(node.getOrder());
            availableVoList.add(businessLineVo);
        }
        return ResultUtils.buildResult(ResultCode.OK, availableVoList);
    }

    @Override
    public Result listUserAvailableMenu(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        UserContext userContext = getUserContext();
        BaseTree optTree = userContext.getBusinessLineUserOptTree(businessLine);
        List<String> resIdList = new ArrayList<>();
        if(optTree != null){
            for (String resId : optTree.getNodeMapping().keySet()) {
                if(! "0".equals(resId)){
                    resIdList.add(resId);
                }
            }
        }
        if(resIdList.size() == 0){
            return ResultUtils.buildResult(ResultCode.NO_AVAILABLE_MENU);
        }
        List<OperationResource> operationResourceList = operationResourceDao.searchAndOrder(resIdList);
        List<MenuVo> menuVoList = new ArrayList<>();
        for (OperationResource operationResource : operationResourceList) {
            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(operationResource, menuVo);
            menuVoList.add(menuVo);
        }
        return ResultUtils.buildResult(ResultCode.OK, menuVoList);
    }

    @Override
    public Result getDashboard(String businessLine) {
        UserContext userContext = getUserContext();
//        BaseNode node = userContext.getDashboard(businessLine);
        BaseNode node = null;
        List<ResTree> nodeList = resourceTreeService.listChildrenNodesByType(ResourceType.DASHBOARD.value(), businessLine);
        if(! CollectionUtils.isEmpty(nodeList)){
            ResTree lastNode = null;
            for (ResTree tempNode : nodeList) {
                if(lastNode == null || lastNode.getCreateTime().before(tempNode.getCreateTime())){
                    lastNode = tempNode;
                }
            }
            node = new ResTreeNode(lastNode);
        }
        ReportPermissionVo dashboardPermissionVo = new ReportPermissionVo();
        // 获得大盘操作权限
        Map<String, String> dashboardPermissions = getDashboardOptPermission(businessLine, node);

        if(node != null){
            Dashboard dashboard = dashboardDao.getDashboardByResId(node.getId());
            BeanUtils.copyProperties(dashboard, dashboardPermissionVo);
            List<ComponentPermissionVo> componentPermissionVoList =
                    userComponentService.getWidgetsPermission(dashboard.getLayoutJson(), businessLine);
            dashboardPermissionVo.setWidgetsPermission(componentPermissionVoList);
        }
        dashboardPermissionVo.setPermissions(dashboardPermissions);
        return ResultUtils.buildResult(ResultCode.OK, dashboardPermissionVo);
    }

     private Map<String, String> getDashboardOptPermission(String businessLine, BaseNode dashboardNode){
         if(dashboardNode == null){
             return userComponentService.getBusinessLineLevelPermission(businessLine, ResourceType.DASHBOARD);
         }else{
             Map<String, String> dashboardPermissions = userComponentService.getComponentOptPermission(dashboardNode, businessLine);
             String[] BusinessLineLevelPermissions = ResourceType.getBusinessLineLevelPermission(ResourceType.DASHBOARD.value());
             for (String businessLineLevelPermissions : BusinessLineLevelPermissions){
                 dashboardPermissions.remove(businessLineLevelPermissions);
             }
            return dashboardPermissions;
        }
    }

    private UserContext getUserContext() {
        String token = TokenUtils.getToken();
        return userContextContainer.getUserContext(token);
    }
}
