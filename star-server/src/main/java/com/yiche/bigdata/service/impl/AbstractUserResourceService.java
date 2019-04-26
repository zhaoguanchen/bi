package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.constants.RoleType;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Permission;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.vo.CommonResourceVo;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.service.core.*;
import com.yiche.bigdata.utils.EnumUtils;
import com.yiche.bigdata.utils.PaginationUtil;
import com.yiche.bigdata.utils.ResultUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractUserResourceService {

    @Autowired
    protected ResourceCenter resourceCenter;

    @Autowired
    protected UserContextContainer userContextContainer;

    @Autowired
    protected UserComponentService userComponentService;

    @Autowired
    protected ResourceTreeService resourceTreeService;

    protected Result getResourceList(String pid, String search) {
        UserContext userContext = getUserContext();
        ResTree resTree = resourceTreeService.getNodeById(pid);
        if (resTree == null) {
            return ResultUtils.buildResult(ResultCode.NODE_NOT_EXIST);
        }
        BaseNode parentNode = userContext.getResourceNode(pid);
        if (parentNode == null) {
            parentNode = new ResTreeNode<>(resourceTreeService.getNodeById(pid));
            if (parentNode == null || !parentNode.getCreater().equals(userContext.getUserInfo().getUserName())) {
                return ResultUtils.buildResult(ResultCode.NO_PERMISSION);
            }
        }
        String businessLine = pid;
        if (!parentNode.getType().equals(ResourceType.BUSINESS_LINE)) {
            businessLine = resTree.getBusinesLine();
        }
        RoleType roleType = userContext.getMaxRole(businessLine);
        List<ResTree> fixedNodeList = getFixedNodes(parentNode.getChildrenNodes(), pid, userContext);
        List<CommonResourceVo> personalResVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(fixedNodeList)) {
            for (ResTree node : fixedNodeList) {
                conditionFilter(search, roleType, personalResVoList, node);
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, personalResVoList);
    }

    protected List<ResTree> getFixedNodes(List<BaseNode> cachedNodes, String resId, UserContext userContext) {
        List<ResTree> fixedNodeList = new ArrayList<>();
        List<ResTree> nodeList = getLastResTreeNodes(cachedNodes);
        //用户新添加的资源，还未放到缓存中
        nodeList.addAll(getNewCreateTreeNodes(cachedNodes, resId, userContext));
        if (!CollectionUtils.isEmpty(nodeList)) {
            for (ResTree node : nodeList) {
                if (CollectionUtils.arrayToList(getResourceType()).contains(EnumUtils.valueOf(ResourceType.class, node.getType()))) {
                    fixedNodeList.add(node);
                }
            }
        }
        return fixedNodeList;
    }


    protected List<ResTree> getLastResTreeNodes(List<BaseNode> cachedNodes) {
        List<ResTree> nodeList = new ArrayList<>();
        List<String> cachedResIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cachedNodes)) {
            for (BaseNode node : cachedNodes) {
                cachedResIds.add(node.getId());
            }
        }
        //更新缓存中的资源信息
        List<ResTree> cachedResList = resourceTreeService.getNodeByIdList(cachedResIds);
        if (!CollectionUtils.isEmpty(cachedResList)) {
            nodeList.addAll(cachedResList);
        }
        return nodeList;
    }


    protected List<ResTree> getNewCreateTreeNodes(List<BaseNode> cachedNodes, String pid, UserContext userContext) {
        List<String> cachedResIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cachedNodes)) {
            for (BaseNode node : cachedNodes) {
                cachedResIds.add(node.getId());
            }
        }
        return resourceTreeService.listNodeByCreator(userContext.getUserId(), pid, cachedResIds);
    }

    protected void conditionFilter(String search, RoleType roleType, List<CommonResourceVo> resultVoList, ResTree node) {
        DomainUserInfo domainUserInfo = resourceCenter.getUser(node.getCreater());
        String realName = node.getCreater();
        if (domainUserInfo != null) {
            realName = domainUserInfo.getRealName();
        }
        boolean personalRes = false;
        if (getUserContext().getResTree().getNodeMapping().get(node.getId()) == null) {
            personalRes = true;
        }
        if (StringUtils.isNotEmpty(search)) {
            if (StringUtils.containsIgnoreCase(node.getName(), search)) {
                addToListAndSort(roleType, resultVoList, node, realName, personalRes);
            } else if (domainUserInfo != null && StringUtils.containsIgnoreCase(domainUserInfo.getRealName(), search)) {
                addToListAndSort(roleType, resultVoList, node, realName, personalRes);
            }
        } else {
            addToListAndSort(roleType, resultVoList, node, realName, personalRes);
        }
    }

    private void addToListAndSort(RoleType roleType, List<CommonResourceVo> resultVoList, ResTree node, String realName, boolean personalRes) {
        CommonResourceVo commonResourceVo = buildResourceVo(node, roleType, realName, personalRes);
        if (CollectionUtils.isEmpty(resultVoList)) {
            resultVoList.add(commonResourceVo);
        } else {
            boolean added = false;
            for (int i = 0; i < resultVoList.size(); i++) {
                if (compareCommonResourceVo(commonResourceVo, resultVoList.get(i))) {
                    resultVoList.add(i, commonResourceVo);
                    added = true;
                    break;
                }
            }
            if (!added) {
                resultVoList.add(commonResourceVo);
            }
        }
    }

    protected boolean compareCommonResourceVo(CommonResourceVo left, CommonResourceVo right) {
        return left.getCreateTime().after(right.getCreateTime());
    }


    public Result getPagedResource(PagedQueryItem<Map> queryItem) {
        String search = (String) queryItem.getCondition().get("search");
        String pid = (String) queryItem.getCondition().get("pid");
        if (org.springframework.util.StringUtils.isEmpty(pid)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Result allResult = this.getResourceList(pid, search);
        return getPagedResult(queryItem, allResult);
    }

    protected Result getPagedResult(PagedQueryItem<Map> queryItem, Result allResult) {
        if (allResult.getCode() == ResultCode.OK.value()) {
            List<CommonResourceVo> voList = (List<CommonResourceVo>) allResult.getResult();
            int start = PaginationUtil.startValue(queryItem.getPageNo(), queryItem.getPageSize());
            int end = PaginationUtil.endValue(queryItem.getPageNo(), queryItem.getPageSize());
            int totalPage = PaginationUtil.totalPage(voList.size(), queryItem.getPageSize());
            List<CommonResourceVo> pagedVoList = new ArrayList<>();
            if (end >= voList.size()) {
                end = voList.size() - 1;
            }
            if (start < voList.size()) {
                pagedVoList = voList.subList(start, end + 1);
            }
            resourceVoPostProcessing(pagedVoList);
            Pagination<CommonResourceVo> resultVo = new Pagination<>(voList.size(), queryItem.getPageNo(), totalPage, pagedVoList);
            return ResultUtils.buildResult(ResultCode.OK, resultVo);

        } else {
            return allResult;
        }
    }

    protected CommonResourceVo buildResourceVo(ResTree node, RoleType roleType, String realName, boolean personalRes) {
        CommonResourceVo commonResourceVo = new CommonResourceVo();
        BeanUtils.copyProperties(node, commonResourceVo);
        commonResourceVo.setType(EnumUtils.valueOf(ResourceType.class, node.getType()));
        String permissionKeys = getUserContext().getResourcePermissions().get(node.getId());
        String nodeType = Integer.toString(node.getType());
        Map<String, String> permissions = getResourcePermissionMap(roleType, permissionKeys, nodeType, personalRes);
        permissions.remove("view");
        commonResourceVo.setPermissions(permissions);
        Widget widget = resourceCenter.getWidgets().get(node.getId());
        if (widget != null) {
            commonResourceVo.setJson(widget.getDataJson());
        }
        commonResourceVo.setCreater(realName);
        return commonResourceVo;
    }

    protected void resourceVoPostProcessing(List<CommonResourceVo> voList) {
    }


    protected Map<String, String> getResourcePermissionMap(RoleType roleType, String permissionKeys, String nodeType, boolean personalRes) {
        Map<String, String> permissions = new HashMap<>();
        if (personalRes || roleType.equals(RoleType.SUPER_ADMIN_TYPE) || roleType.equals(RoleType.BUSINESS_ADMIN_TYPE)) {
            List<Permission> adminPermissions = resourceCenter.getResOpt().get(nodeType);
            for (Permission permission : adminPermissions) {
                permissions.put(permission.getKey(), permission.getName());
            }
            return permissions;
        } else {
            return userComponentService.getPermissionObjMap(permissionKeys);
        }
    }

    protected UserContext getUserContext() {
        String token = TokenUtils.getToken();
        return userContextContainer.getUserContext(token);
    }

    abstract ResourceType[] getResourceType();
}
