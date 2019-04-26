package com.yiche.bigdata.utils;

import com.yiche.bigdata.entity.generated.ResTree;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Map;

public class ResTreeUtils {

    public static ResTree buildResTreeNode( Map<String, Object> param){
        ResTree resTreeNode = new ResTree();
        if(param.get("resId") == null){
            resTreeNode.setId(CommonUtils.getUUID());
        }else{
            resTreeNode.setId((String) param.get("resId"));
        }
        resTreeNode.setName(((String) param.get("name")).trim());
        resTreeNode.setPid((String) param.get("pid"));
        resTreeNode.setType((Integer) param.get("type"));
        resTreeNode.setSort((Integer) param.get("order"));
        resTreeNode.setDescription(((String) param.get("description")));
        if(param.get("businessLine") != null){
            resTreeNode.setBusinesLine((String) param.get("businessLine"));
        }
        resTreeNode.setCreater((String) param.get("operator"));
        resTreeNode.setCreateTime(new Date());
        resTreeNode.setLastModifier((String) param.get("operator"));
        resTreeNode.setLastModifyTime(new Date());
        return resTreeNode;
    }

    public static ResTree updateResTreeNode( Map<String, Object> param){
        ResTree resTreeNode = new ResTree();
        if(param.get("name") != null){
            resTreeNode.setName(((String) param.get("name")).trim());
        }
        if(param.get("pid") != null){
            resTreeNode.setPid((String) param.get("pid"));
        }
        if(param.get("order") != null){
            resTreeNode.setSort((Integer) param.get("order"));
        }
        if(param.get("description") != null){
            resTreeNode.setDescription(((String) param.get("description")));
        }
        if(param.get("operator") != null){
            resTreeNode.setLastModifier((String) param.get("operator"));
        }
        resTreeNode.setLastModifyTime(new Date());
        return resTreeNode;
    }
}
