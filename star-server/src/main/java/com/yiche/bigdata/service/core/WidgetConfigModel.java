package com.yiche.bigdata.service.core;

import com.yiche.bigdata.entity.dto.search.AttrReq;

import java.util.List;
import java.util.Map;

public class WidgetConfigModel {

    private String searchConfig;

    private String timeFilterString;

    private boolean hasLinkRatio;

    private boolean hasYoYRatio;

    //解析表达式时，缓存在expressions中
    Map<String, String> expressions;

    //聚合后排序过滤配置，缓存在aggFilters中
    List<AttrReq> aggFilters;

    public WidgetConfigModel(){

    }

    public WidgetConfigModel(String searchConfig, String timeFilterString,
                             boolean hasLinkRatio, boolean hasYoYRatio,
                             Map<String, String> expressions, List<AttrReq> aggFilters) {
        this.searchConfig = searchConfig;
        this.timeFilterString = timeFilterString;
        this.hasLinkRatio = hasLinkRatio;
        this.hasYoYRatio = hasYoYRatio;
        this.expressions = expressions;
        this.aggFilters = aggFilters;
    }

    public String getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(String searchConfig) {
        this.searchConfig = searchConfig;
    }

    public String getTimeFilterString() {
        return timeFilterString;
    }

    public void setTimeFilterString(String timeFilterString) {
        this.timeFilterString = timeFilterString;
    }

    public boolean isHasLinkRatio() {
        return hasLinkRatio;
    }

    public void setHasLinkRatio(boolean hasLinkRatio) {
        this.hasLinkRatio = hasLinkRatio;
    }

    public boolean isHasYoYRatio() {
        return hasYoYRatio;
    }

    public void setHasYoYRatio(boolean hasYoYRatio) {
        this.hasYoYRatio = hasYoYRatio;
    }

    public Map<String, String> getExpressions() {
        return expressions;
    }

    public void setExpressions(Map<String, String> expressions) {
        this.expressions = expressions;
    }

    public List<AttrReq> getAggFilters() {
        return aggFilters;
    }

    public void setAggFilters(List<AttrReq> aggFilters) {
        this.aggFilters = aggFilters;
    }
}
