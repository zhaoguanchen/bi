package com.yiche.bigdata.service.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseTree {

    private BaseNode rootNode;

    private Map<String, BaseNode> nodeMapping = new HashMap<String, BaseNode>();

    public BaseTree(){
    }

    public BaseTree(BaseNode rootNode){
        this.rootNode = rootNode;
        this.addNodeMapping(rootNode);
    }

    protected void addNodeMapping(BaseNode node) {
        this.nodeMapping.put(node.getId(), node);
    }

    public BaseNode getRootNode(){
        return this.rootNode;
    }

    protected void setRootNode(BaseNode rootNode) {
        this.rootNode = rootNode;
        this.addNodeMapping(rootNode);
    }

    public Map<String, BaseNode> getNodeMapping() {
        return nodeMapping;
    }

    protected void clearNodeMapping() {
        this.nodeMapping = new HashMap<String, BaseNode>();
    }

    public BaseNode findNode(String id){
        if(this.nodeMapping.get(id) == null){
            return null;
        }
        return this.nodeMapping.get(id);
    }

    //树过滤是根据原树和过滤条件构造一个新树
    public BaseTree filter(Set<String> ids, int deep){
        BaseTree filteredTree = new BaseTree();
        BaseNode node = this.getRootNode().filter(filteredTree, ids, deep);
        if(node != null){
            filteredTree.setRootNode(this.getRootNode().filter(filteredTree, ids, deep));
        }
        return filteredTree;
    }

}
