package com.yiche.bigdata.dataprovider.config;

import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.dto.req.AttrReq;
import com.yiche.bigdata.dto.req.ValueTupleReq;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yfyuan on 2017/1/17.
 */
public class DimensionConfig extends ConfigComponent {

    private String columnName;
    private String filterType;
    private List<String> values;
    private String id;
    private String custom;

    public static DimensionConfig builtFromAttrReq(AttrReq attrReq) {
        DimensionConfig dimensionConfig = new DimensionConfig();
        final String columnName = attrReq.getColumnName();
        final String filterType = attrReq.getFilterType();
        final String id = attrReq.getId();

        List<ValueTupleReq> valueTupleReqList = attrReq.getValues();
        List<String> values = CollectionUtils.isEmpty(valueTupleReqList) ?
                Collections.EMPTY_LIST :
                valueTupleReqList.stream().map(valueTupleReq -> {
                    return JSONObject.toJSONString(valueTupleReq);
                }).collect(Collectors.toList());

        dimensionConfig.setColumnName(columnName);
        dimensionConfig.setFilterType(filterType);
        dimensionConfig.setId(id);
        dimensionConfig.setValues(values);
        return dimensionConfig;
    }

    public boolean hasRelativeValues() {
        long num = Long.MIN_VALUE;
        if (!CollectionUtils.isEmpty(values)) {
            num = values.stream().filter(value -> StringUtils.isNotEmpty(value) && value.contains("now")).count();
        }
        return num > 0;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public Iterator<ConfigComponent> getIterator() {
        return null;
    }

    @Override
    public String toString() {
        return "DimensionConfig{" +
                "columnName='" + columnName + '\'' +
                ", filterType='" + filterType + '\'' +
                ", values=" + values +
                ", id='" + id + '\'' +
                ", custom='" + custom + '\'' +
                '}';
    }
}
