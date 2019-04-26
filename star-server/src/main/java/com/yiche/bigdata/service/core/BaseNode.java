package com.yiche.bigdata.service.core;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.utils.EnumUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class BaseNode implements Cloneable {

    private String id;

    private String name;

    private String pid;

    private ResourceType type;

    private Integer order;

    private String description;

    private String creater;

    private Date createTime;

    private String lastModifier;

    private Date lastModifyTime;

    private boolean hasLoadChild;

    private List<BaseNode> childrenNodes;

    public BaseNode(){
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getId() {
        return id;
    }

    public String getPid() {
        return pid;
    }

    public ResourceType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreater() {
        return creater;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public boolean isHasLoadChild() {
        return hasLoadChild;
    }

    public List<BaseNode> getChildrenNodes() {
        return childrenNodes;
    }

    protected void clearChildren(){
        this.hasLoadChild = false;
        childrenNodes = new ArrayList<BaseNode>();
    }

    protected void addChildren(BaseNode node){
        if(this.childrenNodes ==  null){
            childrenNodes = new ArrayList<>();
            childrenNodes.add(node);
            return;
        }
        if(this.childrenNodes.size() == 0){
            childrenNodes.add(node);
            return;
        }
        for (int i = 0; i < this.childrenNodes.size(); i++) {
            if(node.order < this.childrenNodes.get(i).order){
                this.childrenNodes.add(i, node);
                return;
            }
        }
        this.hasLoadChild = true;
        childrenNodes.add(node);
    }

    public BaseNode filter(BaseTree tree, Set<String> ids, int deep){
        if(deep == 0){
            return null;
        }
        if(!ids.contains(this.id)){
            return null;
        }
        BaseNode filteredNode = (BaseNode) this.clone();
        if(filteredNode == null){
            return null;
        }
        deep--;
        filteredNode.clearChildren();
        tree.addNodeMapping(filteredNode);
        if(this.childrenNodes != null){
            for (BaseNode node : this.childrenNodes) {
                BaseNode childNode = node.filter(tree, ids, deep);
                if (childNode != null) {
                    filteredNode.addChildren(childNode);
                }
            }
        }
        return filteredNode;
    }

    public BaseTree filter(BaseNode node, Set<String> ids, int deep){
        BaseTree filteredTree = new BaseTree();
        filteredTree.setRootNode(node.filter(filteredTree, ids, deep));
        return filteredTree;
    }

    @Override
    public Object clone(){
        BaseNode node = null;
        try{
            node = (BaseNode) super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return node;
    }

}
