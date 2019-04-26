package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.ResTree;

import java.util.List;
import java.util.Map;

public interface ResourceTreeService {

    int getNextOrderValue(String pid);

    Result addNode(Map<String, Object> param);

    List<ResTree> listAllNodeByType(Integer type);

    List<ResTree> listBusinessLineResource(String businessLine);

    List<ResTree> listBusinessLineNodeByType(Integer type, String businessLine);

    List<ResTree> listChildren(String pid);

    List<ResTree> listChildrenNodesByType(Integer type, String pid);

    List<ResTree> listNodeByCreator(String userId);

    List<ResTree> listNodeByCreator(String userId, String pid, List<String> filter);

    List<ResTree> listAllBusinessLineResByCreator(String userId, Integer resType, String businessLine, List<String> filter);

    ResTree getNodeById(String resId);

    List<ResTree> getNodeByIdList(List<String> resIdList);

    Result updateNode(String resId, Map<String, Object> param);

    Result deleteNodeById(String resId);

    ResTree getNodeTypeAndName(String name, Integer nodeType);

    ResTree getNodeTypeAndName(String name, Integer nodeType, String businessLine);

    boolean checkNodeName(String name, Integer nodeType);

    boolean checkNodeName(String name, Integer nodeType, String pid);

    boolean checkNodeNameOfBusiness(String name, Integer nodeType, String businessLine);
}
