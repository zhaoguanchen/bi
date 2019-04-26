package com.yiche.bigdata.entity.vo;
import java.util.List;

public class ResourceSelectorVo {
    private String resId;
    private String name;
    private String type;
    private String relyResId;
    private List<ResourceSelectorVo> children;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ResourceSelectorVo> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceSelectorVo> children) {
        this.children = children;
    }

    public String getRelyResId() {
        return relyResId;
    }

    public void setRelyResId(String relyResId) {
        this.relyResId = relyResId;
    }
}
