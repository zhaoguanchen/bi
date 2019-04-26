package com.yiche.bigdata.service.core;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.dao.ResTreeDao;
import com.yiche.bigdata.entity.generated.*;
import com.yiche.bigdata.utils.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    @Autowired
    private ResTreeDao resTreeDao;

    public BaseNode loadRootNode(BaseTree tree){
        ResTree resNode = resTreeDao.findByPrimaryKey("0");
        if(resNode == null){
            return null;
        }
        ResTreeNode node = new ResTreeNode(resNode);
        tree.setRootNode(node);
        return  node;
    }

    public void loadChildNode(BaseTree tree, BaseNode parentNode){
        List<ResTree> childNodes = resTreeDao.findChildren(parentNode.getId());
        for (ResTree childNode : childNodes) {
            BaseNode node = createTreeNode(childNode);
            tree.addNodeMapping(node);
            parentNode.addChildren(node);
            loadChildNode(tree, node);
        }
    }
    
    //加载各种资源的实体数据
    private BaseNode createTreeNode(ResTree resTree){
        ResourceType type = EnumUtils.valueOf(ResourceType.class, resTree.getType());
        BaseNode node = null;
        switch (type){
            case DATA_TABLE:
                node = new ResTreeNode<DataTable>(resTree);
                loadDataTableContent(node);
                break;
            case DATA_SET:
                node = new ResTreeNode<Dataset>(resTree);
                loadDataSetContent(node);
                break;
            case REPORT:
                node = new ResTreeNode<Report>(resTree);
                loadReportContent(node);
                break;
            case DASHBOARD:
                node = new ResTreeNode<Dashboard>(resTree);
                loadDashboardContent(node);
                break;
            case WIDGET:
                node = new ResTreeNode<Widget>(resTree);
                loadWidgetContent(node);
                break;
            default:
                node = new ResTreeNode(resTree);
        }
        return node;
    }

    private void loadDataTableContent(BaseNode node){

    }

    private void loadDataSetContent(BaseNode node){

    }

    private void loadDashboardContent(BaseNode node){

    }

    private void loadReportContent(BaseNode node){

    }

    private void loadWidgetContent(BaseNode node){

    }

}
