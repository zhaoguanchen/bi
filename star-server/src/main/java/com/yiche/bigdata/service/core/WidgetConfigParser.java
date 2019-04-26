package com.yiche.bigdata.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.entity.dto.search.*;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.utils.ExpressionUtils;
import com.yiche.bigdata.utils.NumberValidationUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class WidgetConfigParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetConfigParser.class);

    public static String[] SORT = {"top", "bottom"};

    public WidgetConfigModel parse(JSONObject config, Dataset dataset, String filter){
        CfgReq cfgReq = new CfgReq();
        //聚合后排序过滤配置，缓存在aggFilters中
        List<AttrReq> aggFilters = new ArrayList<>();
        //行维
        JSONArray rows = config.getJSONArray("keys");
        cfgReq.setRows(getColumns(rows, aggFilters));
        //列维
        JSONArray columns = config.getJSONArray("groups");
        cfgReq.setColumns(getColumns(columns, aggFilters));
        //度量
        JSONArray measures = config.getJSONArray("values");
        //解析表达式时，缓存在expressions中
        Map<String, String> expressions = new LinkedHashMap<>();
        cfgReq.setValues(getMeasures(measures, expressions, aggFilters));
        //图表定义中的过滤
        JSONArray filters = config.getJSONArray("filters");
        //查询时所有的过滤条件
        List<AttrReq> allFilters = getFilters(filters);
        //合并数据集过滤条件,
        allFilters.addAll(getDataSetFilters(dataset.getDataJson()));
        //合并报表过滤条件
        allFilters.addAll(getReportFilters(filter, dataset.getResId()));
        //序列化查询参数
        String cfgString = JSONObject.toJSONString(cfgReq);
        cfgReq.setFilters(allFilters);

        String timeFilterString = getFilterString(dataset.getDataJson());

        //获得环比同比配置
        Boolean hasLinkRatio = config.getBoolean("month_on_month");
        Boolean hasYoYRatio = config.getBoolean("year_on_year");

       return new WidgetConfigModel(cfgString, timeFilterString,
                hasLinkRatio == null ? false : hasLinkRatio,
                hasYoYRatio == null ? false : hasYoYRatio,
                expressions, aggFilters);

    }

    //解析行维与列维配置
    private List<AttrReq> getColumns(JSONArray jsonArray, List<AttrReq> aggFilters){
        List<AttrReq> columns = new ArrayList<>();
        if(jsonArray != null && jsonArray.size() > 0){
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject column = jsonArray.getJSONObject(i);
                AttrReq attr = new AttrReq();
                attr.setColumnName(column.getString("col"));
                if(StringUtils.isEmpty(attr.getColumnName())){
                    attr.setColumnName(column.getString("column"));
                }
                attr.setFilterType(column.getString("type"));
                attr.setId(column.getString("id"));
                attr.setValues(getValuesFromJson(column.getJSONArray("values")));
                columns.add(attr);

                getSortInfo(aggFilters, column, attr.getColumnName());
            }
        }
        return columns;
    }

    //解析通用的value json
    private List<ValueTupleReq> getValuesFromJson(JSONArray valuesJson) {
        List<ValueTupleReq> values = new ArrayList<>();
        if(valuesJson != null && valuesJson.size() > 0){
            for (int i = 0; i < valuesJson.size(); i++) {
                JSONObject value = valuesJson.getJSONObject(i);
                ValueTupleReq valueTupleReq = new ValueTupleReq();
                valueTupleReq.setKey(value.getString("key"));
                valueTupleReq.setValue(value.getString("value"));
                values.add(valueTupleReq);
            }
        }
        return values;
    }


    private List<ValueReq> getMeasures(JSONArray jsonArray, Map<String, String> expressions, List<AttrReq> aggFilters){
        List<ValueReq> measures = new ArrayList<>();
        if(jsonArray != null && jsonArray.size() > 0) {
            //逐个解析指标与表达式
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray measureJson = jsonArray.getJSONObject(i).getJSONArray("cols");
                if(measureJson != null && measureJson.size() > 0){
                    for (int j = 0; j < measureJson.size(); j++) {
                        JSONObject measure = measureJson.getJSONObject(j);
                        ValueReq valueReq = new ValueReq();
                        String temp = "";
                        if(measure.getString("type") != null ){
                            if("exp".equals(measure.getString("type"))){
                                String expression = measure.getString("exp");
                                if(StringUtils.isNotEmpty(expression)){
                                    measures.addAll(ExpressionUtils.getMeasures(expression));
                                    String alias = measure.getString("alias");
                                    expressions.put(alias, expression);
                                    temp = expression;
                                }
                            }
                        }else {
                            valueReq.setColumn(measure.getString("col"));
                            valueReq.setAggType(measure.getString("aggregate_type"));
                            measures.add(valueReq);
                        }

                        //聚合后排序配置解析, 只有第一个生效
                        String columnKey;
                        if(StringUtils.isEmpty(temp)){
                            columnKey = ExpressionUtils.getAggExpression(valueReq.getColumn(), valueReq.getAggType());
                        }else {
                            columnKey = temp;
                        }
                        getSortInfo(aggFilters, measure, columnKey);

                        //聚合后过滤条件解析
                        String filterType = measure.getString("f_type");
                        JSONArray filterValues = measure.getJSONArray("f_values");
                        if(StringUtils.isNotEmpty(filterType) &&
                                filterValues != null && filterValues.size() > 0){
                            AttrReq attrReq = new AttrReq();
                            attrReq.setColumnName(columnKey);
                            attrReq.setFilterType(filterType);
                            List<ValueTupleReq> values = new ArrayList<>();
                            for (int k = 0; k < filterValues.size(); k++) {
                                String value = measure.getJSONArray("f_values").get(k).toString();
                                ValueTupleReq valueTupleReq = new ValueTupleReq();
                                valueTupleReq.setKey(value);
                                valueTupleReq.setValue(value);
                                values.add(valueTupleReq);
                            }
                            attrReq.setValues(values);
                            aggFilters.add(attrReq);
                        }
                    }
                }
            }
        }

        //多个表达式可能解析出同一个聚合指标,所以需要去重
        List<String> temp = new ArrayList<>();
        List<ValueReq> result = new ArrayList<>();
        for (ValueReq valueReq : measures) {
            String key = valueReq.getAggType() + "-" + valueReq.getColumn();
            if(! temp.contains(key)){
                result.add(valueReq);
                temp.add(key);
            }
        }
        return result;
    }

    private void getSortInfo(List<AttrReq> aggFilters, JSONObject column, String columnKey) {
        boolean hasAggSortFlag = false;
        for(AttrReq attrReq : aggFilters){
            if(ArrayUtils.contains(SORT, attrReq.getFilterType())){
                hasAggSortFlag = true;
            }
        }

        if(!hasAggSortFlag){
            String f_top = column.getString("f_top");
            String f_bottom = column.getString("f_bottom");
            String f_value = null;
            AttrReq attrReq = new AttrReq();
            if((StringUtils.isNotEmpty(f_top)) && NumberValidationUtils.isNumber(f_top)){
                attrReq.setFilterType("top");
                f_value = f_top;
            }else if((StringUtils.isNotEmpty(f_bottom)) && NumberValidationUtils.isNumber(f_bottom)){
                attrReq.setFilterType("top");
                f_value = f_top;
            }
            if(StringUtils.isNotEmpty(f_value)){
                attrReq.setColumnName(columnKey);
                List<ValueTupleReq> values = new ArrayList<>();
                ValueTupleReq valueTupleReq = new ValueTupleReq();
                valueTupleReq.setKey(f_value);
                valueTupleReq.setValue(f_value);
                values.add(valueTupleReq);
                attrReq.setValues(values);
                aggFilters.add(attrReq);
            }
        }
    }


    private List<AttrReq> getFilters(JSONArray jsonArray){
        List<AttrReq> filters = new ArrayList<>();
        if(jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray filtersJson = jsonArray.getJSONObject(i).getJSONArray("filters");
                if(filtersJson != null){
                    parseFilters(filtersJson, filters, null);
                }else{
                    parseFilters(jsonArray, filters, null);
                    break;
                }
            }
        }
        return filters;
    }

    //报表中配置的过滤条件
    private List<AttrReq> getReportFilters(String filtersStr, String dataSetId){
        List<AttrReq> filters = new ArrayList<>();
        if(StringUtils.isNotEmpty(filtersStr)){
            try {
                JSONArray filtersJson = JSONObject.parseArray(filtersStr);
                parseFilters(filtersJson, filters, dataSetId);
            }catch (JSONException exception){
                LOGGER.error("widget data json parse failure [{}] ... ", filtersStr, exception);
            }
        }
        return filters;
    }

    //解析通用格式的过滤条件
    private void parseFilters(JSONArray filtersJson, List<AttrReq> filters, String widgetDataSetId) {
        if(filtersJson != null && filtersJson.size() > 0){
            for (int j = 0; j < filtersJson.size(); j++) {
                AttrReq attr = new AttrReq();
                JSONObject filterJson = filtersJson.getJSONObject(j);
                if(StringUtils.isNotEmpty(widgetDataSetId)){
                    JSONArray col = filterJson.getJSONArray("col");
                    if(col == null || col.size() == 0){
                        continue;
                    }
                    //报表可能包含多个dataSet的的过滤条件，只计算图表中的过滤条件
                    String dataSetId = col.getJSONObject(0).getString("datasetId");
                    if(! widgetDataSetId.equals(dataSetId)){
                        continue;
                    }
                    JSONObject columnName = col.getJSONObject(0).getJSONObject("column");
                    if(columnName != null){
                        attr.setColumnName(columnName.getString("key"));
                    }
                }else {
                    attr.setColumnName(filterJson.getString("col"));
                }
                attr.setFilterType(filterJson.getString("type"));
                attr.setValues(getValuesFromJson(filterJson.getJSONArray("values")));
                if(checkFilter(attr)){
                    filters.add(attr);
                }
            }
        }
    }


    private boolean checkFilter(AttrReq attrReq){
        if(StringUtils.isEmpty(attrReq.getColumnName())
                || StringUtils.isEmpty(attrReq.getFilterType())
                || CollectionUtils.isEmpty(attrReq.getValues())){
            return false;
        }
        return true;
    }


    //数据集中配置的过滤条件
    private List<AttrReq> getDataSetFilters(String json) {
        List<AttrReq> filters = new ArrayList<>();
        if(StringUtils.isNotEmpty(json)){
            try {
                JSONObject dataJson = JSONObject.parseObject(json);
                JSONArray filtersJson = dataJson.getJSONArray("filters");
                filters = getFilters(filtersJson);
            }catch (JSONException exception){
                LOGGER.error("widget data json parse failure [{}] ... ", json, exception);
            }
        }
        return filters;
    }

    private String getFilterString(String dataSetJson){
        TimeFilterReq timeFilterReq = new TimeFilterReq();
        if(StringUtils.isNotEmpty(dataSetJson)) {
            try {
                JSONObject dataJson = JSONObject.parseObject(dataSetJson);
                JSONObject timePrimaryJson = dataJson.getJSONObject("timePrimaryKey");
                String column = timePrimaryJson.getString("column");
                timeFilterReq.setTimePrimaryColumn(column);
            } catch (JSONException exception) {
                LOGGER.error("widget data json parse failure [{}] ... ", dataSetJson, exception);
            }
        }
        return JSONObject.toJSONString(timeFilterReq);
    }

}
