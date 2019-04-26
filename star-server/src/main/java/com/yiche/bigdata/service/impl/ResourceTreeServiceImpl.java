package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.ResTreeDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.service.UserInfoService;
import com.yiche.bigdata.utils.ResTreeUtils;
import com.yiche.bigdata.utils.ResultUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceTreeServiceImpl implements ResourceTreeService {

    @Autowired
    private ResTreeDao resTreeDao;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public int getNextOrderValue(String pid) {
        int order = 0;
        List<ResTree> nodes = resTreeDao.findChildren(pid);
        for (ResTree node : nodes) {
            if (node.getSort() > order) {
                order = node.getSort();
            }
        }
        return ++order;
    }

    @Override
    public Result addNode(Map<String, Object> param) {
        if (param == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (param.get("operator") == null) {
            param.put("operator", userInfoService.getUserNameByToken(TokenUtils.getToken()));
        }
        ResTree treeNode = ResTreeUtils.buildResTreeNode(param);
        if (param.get("order") == null) {
            treeNode.setSort(getNextOrderValue(treeNode.getPid()));
        }
        if (StringUtils.isEmpty(resTreeDao.findByPrimaryKey(treeNode.getPid()))) {
            return ResultUtils.buildResult(ResultCode.PARENT_NODE_NOT_EXIST);
        }
        if (resTreeDao.addTreeNode(treeNode)) {
            Map<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("resId", treeNode.getId());
            return ResultUtils.buildResult(ResultCode.OK, resMap);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    @Override
    public List<ResTree> listAllNodeByType(Integer type) {
        if (type == null) {
            return null;
        }
        return resTreeDao.findAllByType(type);
    }

    @Override
    public List<ResTree> listBusinessLineResource(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return null;
        }
        return resTreeDao.listBusinessLineResource(businessLine);
    }


    @Override
    public List<ResTree> listBusinessLineNodeByType(Integer type, String businessLine) {
        if (StringUtils.isEmpty(businessLine) || type == null) {
            return null;
        }
        return resTreeDao.listBusinessLineNodeByType(type, businessLine);
    }

    @Override
    public List<ResTree> listChildren(String pid) {
        if (StringUtils.isEmpty(pid)) {
            return null;
        }
        return resTreeDao.listChildren(pid);
    }

    @Override
    public List<ResTree> listChildrenNodesByType(Integer type, String pid) {
        if (StringUtils.isEmpty(pid) || type == null) {
            return null;
        }
        return resTreeDao.listChildrenNodesByType(type, pid);
    }

    @Override
    public List<ResTree> listNodeByCreator(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return resTreeDao.listNodesByCreator(userId, null, null);
    }

    @Override
    public List<ResTree> listNodeByCreator(String userId, String pid, List<String> filter) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return resTreeDao.listNodesByCreator(userId, pid, filter);
    }

    @Override
    public List<ResTree> listAllBusinessLineResByCreator(String userId, Integer resType, String businessLine, List<String> filter) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(businessLine)) {
            return null;
        }
        return resTreeDao.listAllBusinessLineResByCreator(userId, resType, businessLine, filter);
    }

    @Override
    public ResTree getNodeById(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return null;
        }
        return resTreeDao.findByPrimaryKey(resId);
    }

    @Override
    public List<ResTree> getNodeByIdList(List<String> resIdList) {
        return resTreeDao.findByIdList(resIdList);
    }

    @Override
    public Result deleteNodeById(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (resTreeDao.findByPrimaryKey(resId) == null) {
            return ResultUtils.buildResult(ResultCode.NODE_NOT_EXIST);
        }
        if (!CollectionUtils.isEmpty(resTreeDao.findChildren(resId))) {
            return ResultUtils.buildResult(ResultCode.DELETE_NODE_FAILURE_CHILD_EXIST);
        }
        if (resTreeDao.deleteNodeById(resId)) {
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DELETE_NODE_FAILURE);
        }
    }

    @Override
    public Result updateNode(String resId, Map<String, Object> param) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (param.get("operator") == null) {
            param.put("operator", userInfoService.getUserNameByToken(TokenUtils.getToken()));
        }
        if (resTreeDao.findByPrimaryKey(resId) == null) {
            return ResultUtils.buildResult(ResultCode.NODE_NOT_EXIST);
        }
        ResTree templet = ResTreeUtils.updateResTreeNode(param);
        templet.setId(resId);
        if (resTreeDao.updateByResId(templet)) {
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.MODIFY_NODE_FAILURE);
        }
    }

    @Override
    public ResTree getNodeTypeAndName(String name, Integer nodeType) {
        List<ResTree> searchResult = resTreeDao.findNodes(name, nodeType);
        if (!CollectionUtils.isEmpty(searchResult)) {
            return searchResult.get(0);
        }
        return null;
    }

    @Override
    public ResTree getNodeTypeAndName(String name, Integer nodeType, String businessLine) {
        List<ResTree> searchResult = resTreeDao.findNodesOfBusiness(name, nodeType, businessLine);
        if (!CollectionUtils.isEmpty(searchResult)) {
            return searchResult.get(0);
        }
        return null;
    }

    @Override
    public boolean checkNodeName(String name, Integer nodeType) {
        if (StringUtils.isEmpty(name) || nodeType == null) {
            return false;
        }
        List<ResTree> searchResult = resTreeDao.findNodes(name, ResourceType.BUSINESS_LINE.value());
        if (CollectionUtils.isEmpty(searchResult)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkNodeName(String name, Integer nodeType, String pid) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(pid) || nodeType == null) {
            return false;
        }
        List<ResTree> searchResult = resTreeDao.findNodes(name, ResourceType.DATA_TABLE_DIRECTORY.value(), pid);
        if (CollectionUtils.isEmpty(searchResult)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkNodeNameOfBusiness(String name, Integer nodeType, String businessLine) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(businessLine) || nodeType == null) {
            return false;
        }
        List<ResTree> searchResult = resTreeDao.findNodesOfBusiness(name, nodeType, businessLine);
        if (CollectionUtils.isEmpty(searchResult)) {
            return true;
        }
        return false;
    }
}
