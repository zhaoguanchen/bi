package com.yiche.bigdata.dao;


import com.yiche.bigdata.entity.generated.OperationResource;
import com.yiche.bigdata.entity.generated.OperationResourceExample;
import com.yiche.bigdata.mapper.generated.OperationResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationResourceDao {

    @Autowired
    private OperationResourceMapper operationResourceMapper;

    public List<OperationResource> searchAndOrder(List<String> idList) {
        OperationResourceExample example = new OperationResourceExample();
        example.createCriteria().andIdIn(idList);
        example.setOrderByClause("`order` asc");
        return operationResourceMapper.selectByExample(example);
    }

    public List<OperationResource> findChildren(String pid) {
        OperationResourceExample example = new OperationResourceExample();
        example.createCriteria().andPidEqualTo(pid);
        return operationResourceMapper.selectByExample(example);
    }

    public OperationResource findByPrimaryKey(String primaryKey) {
        return operationResourceMapper.selectByPrimaryKey(primaryKey);
    }

}
