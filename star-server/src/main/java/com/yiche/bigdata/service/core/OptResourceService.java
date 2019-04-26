package com.yiche.bigdata.service.core;

import com.yiche.bigdata.dao.OperationResourceDao;
import com.yiche.bigdata.entity.generated.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptResourceService {

    @Autowired
    private OperationResourceDao operationResourceDao;

    public BaseNode loadRootNode(BaseTree tree){
        OperationResource resNode = operationResourceDao.findByPrimaryKey("0");
        if(resNode == null){
            return null;
        }
        OptTreeNode node = new OptTreeNode(resNode);
        tree.setRootNode(node);
        return  node;
    }

    public void loadChildNode(BaseTree tree, BaseNode parentNode){
        List<OperationResource> childNodes = operationResourceDao.findChildren(parentNode.getId());
        for (OperationResource childNode : childNodes) {
            if(childNode.getState() > 0){
                continue;
            }
            BaseNode node = new OptTreeNode(childNode);
            tree.addNodeMapping(node);
            parentNode.addChildren(node);
            loadChildNode(tree, node);
        }
    }

}
