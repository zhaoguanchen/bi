package com.yiche.bigdata.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yiche.bigdata.constants.ResourceType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WidgetListVO {

    private String id;

    private String name;

    private String pid;

    private ResourceType type;

    @JsonIgnore
    private Integer order;

    private String description;

    private String creater;

    private Date createTime;

    private String json;

    @JsonIgnore
    private String lastModifier;

    @JsonIgnore
    private Date lastModifyTime;

    Map<String, String> permissions;

    List<WidgetListVO> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public ResourceType getType() {
        return type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Map<String, String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, String> permissions) {
        this.permissions = permissions;
    }

    public List<WidgetListVO> getChildren() {
        return children;
    }

    public void setChildren(List<WidgetListVO> children) {
        this.children = children;
    }

    public WidgetListVO(CommonResourceVo commonResourceVo) {
        this.id = commonResourceVo.getId();
        this.name = commonResourceVo.getName();
        this.pid = commonResourceVo.getPid();
        this.type = commonResourceVo.getType();
        this.order = commonResourceVo.getOrder();
        this.description = commonResourceVo.getDescription();
        this.creater = commonResourceVo.getCreater();
        this.createTime = commonResourceVo.getCreateTime();
        this.json = commonResourceVo.getJson();
        this.lastModifier = commonResourceVo.getLastModifier();
        this.lastModifyTime = commonResourceVo.getLastModifyTime();
        this.permissions = commonResourceVo.getPermissions();
    }
}
