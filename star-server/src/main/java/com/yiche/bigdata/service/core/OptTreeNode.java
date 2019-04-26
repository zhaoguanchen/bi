package com.yiche.bigdata.service.core;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.entity.generated.OperationResource;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.utils.EnumUtils;

public class OptTreeNode extends BaseNode {

    private String key;
    private String code;

    public OptTreeNode(OperationResource operationResource){
        super();
        this.setId(operationResource.getId());
        this.setName(operationResource.getName());
        this.setPid(operationResource.getPid());
        this.setType(EnumUtils.valueOf(ResourceType.class, operationResource.getType()));
        this.setOrder(operationResource.getOrder());
        this.setDescription(operationResource.getDescription());
        this.setCreater(operationResource.getCreater());
        this.setCreateTime(operationResource.getCreateTime());
        this.setLastModifier(operationResource.getLastModifier());
        this.setLastModifyTime(operationResource.getLastModifyTime());
        this.key = operationResource.getKey();
        this.code = operationResource.getCode();
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }
}
