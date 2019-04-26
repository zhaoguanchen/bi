package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.generated.ResTreeExample;
import com.yiche.bigdata.mapper.generated.ResTreeMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class ResTreeDao {

    @Autowired
    private ResTreeMapper resTreeMapper;

    public boolean addTreeNode(ResTree node) {
        int result = resTreeMapper.insert(node);
        return result > 0;
    }

    public List<ResTree> findNodes(String name, Integer nodeType) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andNameEqualTo(name).andTypeEqualTo(nodeType);
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> findNodesOfBusiness(String name, Integer nodeType, String businessLine) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andNameEqualTo(name).andTypeEqualTo(nodeType).andBusinesLineEqualTo(businessLine);
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> findNodes(String name, Integer nodeType, String pid) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andNameEqualTo(name).andTypeEqualTo(nodeType).andPidEqualTo(pid);
        return resTreeMapper.selectByExample(example);
    }


    public boolean deleteNodeById(String resId) {
        return resTreeMapper.deleteByPrimaryKey(resId) > 0;
    }

    public List<ResTree> findChildren(String pid) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andPidEqualTo(pid);
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> findAllByType(Integer type) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andTypeEqualTo(type);
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> listBusinessLineNodeByType(Integer type, String businessLine) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andTypeEqualTo(type).andBusinesLineEqualTo(businessLine);
        return resTreeMapper.selectByExample(example);
    }


    public List<ResTree> listBusinessLineResource(String businessLine) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andBusinesLineEqualTo(businessLine);
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> listChildren(String pid) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andPidEqualTo(pid);
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> listChildrenNodesByType(Integer type, String pid) {
        ResTreeExample example = new ResTreeExample();
        example.createCriteria().andTypeEqualTo(type).andPidEqualTo(pid);
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> listNodesByCreator(String userId, String pid, List<String> filter) {
        ResTreeExample example = new ResTreeExample();
        ResTreeExample.Criteria criteria = example.createCriteria().andCreaterEqualTo(userId);
        if (StringUtils.isNotEmpty(pid)) {
            criteria.andPidEqualTo(pid);
        }
        if (!CollectionUtils.isEmpty(filter)) {
            criteria.andIdNotIn(filter);
        }
        return resTreeMapper.selectByExample(example);
    }

    public List<ResTree> listAllBusinessLineResByCreator(String userId,
                                                         Integer resType,
                                                         String businessLine,
                                                         List<String> filter) {
        ResTreeExample example = new ResTreeExample();
        ResTreeExample.Criteria criteria = example.createCriteria()
                .andCreaterEqualTo(userId).andBusinesLineEqualTo(businessLine);
        if (resType != null) {
            criteria.andTypeEqualTo(resType);
        }
        if (!CollectionUtils.isEmpty(filter)) {
            criteria.andIdNotIn(filter);
        }
        return resTreeMapper.selectByExample(example);
    }


    public ResTree findByPrimaryKey(String resId) {
        return resTreeMapper.selectByPrimaryKey(resId);
    }

    public List<ResTree> findByIdList(List<String> resIdList) {
        if (CollectionUtils.isEmpty(resIdList)) {
            return null;
        }
        ResTreeExample example = new ResTreeExample();
        ResTreeExample.Criteria criteria = example.createCriteria().andIdIn(resIdList);
        return resTreeMapper.selectByExample(example);
    }

    public boolean updateByResId(ResTree node) {
        return resTreeMapper.updateByPrimaryKeySelective(node) > 0;
    }

    public boolean isExistName(String resName, Integer type, String businessLine) {
        int result = resTreeMapper.getResCountByName(resName, type, businessLine);
        return result > 0;
    }


}
