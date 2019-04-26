package com.yiche.bigdata.service.core;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.utils.EnumUtils;

public class ResTreeNode<T> extends BaseNode {

    private String businesLine;

    private T content;

    public ResTreeNode(ResTree resTree){
        super();
        this.setId(resTree.getId());
        this.setName(resTree.getName());
        this.setPid(resTree.getPid());
        this.setType(EnumUtils.valueOf(ResourceType.class, resTree.getType()));
        this.setOrder(resTree.getSort());
        this.setDescription(resTree.getDescription());
        this.setCreater(resTree.getCreater());
        this.setCreateTime(resTree.getCreateTime());
        this.setLastModifier(resTree.getLastModifier());
        this.setLastModifyTime(resTree.getLastModifyTime());
        this.businesLine = resTree.getBusinesLine();
    }

    public String getBusinesLine() {
        return businesLine;
    }

    public T getContent() {
        return content;
    }

}
