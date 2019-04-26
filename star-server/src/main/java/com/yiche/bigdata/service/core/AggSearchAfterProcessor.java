package com.yiche.bigdata.service.core;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.dto.search.AttrReq;
import com.yiche.bigdata.utils.ExpressionUtils;
import com.yiche.bigdata.utils.NumberValidationUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AggSearchAfterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggSearchAfterProcessor.class);

    public void process(Result searchResult, Map<String, String> expressions, List<AttrReq> aggFilters){
        // 表达式值指标计算
        if(!CollectionUtils.isEmpty(expressions) ){
            calculateExpression(expressions, (LinkedHashMap) searchResult.getResult());
        }
        // 聚合后排序过滤计算
        if(!CollectionUtils.isEmpty(aggFilters)){
            resultSortAndFilter(aggFilters, (LinkedHashMap) searchResult.getResult());
        }
    }

    //聚合查询后，逐条记录计算表达式值，添加到结果集
    private void calculateExpression(Map<String, String> expressions, LinkedHashMap resultMap){
        if(resultMap != null && resultMap.get("data") != null){
            Map<String, Integer> columnMap = getColumnAndIndexMap(resultMap);

            //columnList 添加表达式
            ArrayList columnList = (ArrayList) resultMap.get("columnList");
            int maxIndex = columnMap.values().stream().reduce(Integer::max).get();
            for (String alias : expressions.keySet()) {
                maxIndex++;
                String expression = expressions.get(alias);
                LinkedHashMap expressionInfo = new LinkedHashMap();
                expressionInfo.put("index", maxIndex);
                expressionInfo.put("aggType", "expression");
                expressionInfo.put("alias", alias);
                expressionInfo.put("expression", expression);
                columnList.add(expressionInfo);
            }

            //计算表达式值并添加到data
            ArrayList data = (ArrayList) resultMap.get("data");
            for (int i = 0; i < data.size(); i++) {
                ArrayList rowData = (ArrayList) data.get(i);
                List tempList = new ArrayList();
                List<String> expressionErrorList = new ArrayList<>();
                for (String expression : expressions.values()) {
                    if(expressionErrorList.contains(expression)){
                        continue;
                    }
                    try{
                        String result = ExpressionUtils.calculate(columnMap, expression, rowData);
                        tempList.add(result);
                    }catch (Exception exception){
                        expressionErrorList.add(expression);
                        LOGGER.warn("exception calculate error : " + expression);
                        tempList.add("0.0");
                    }
                }
                rowData.addAll(tempList);
            }
        }
    }


    //获取结果集中字段，指标，表达式 与 索引的映射关系
    private Map<String, Integer> getColumnAndIndexMap(LinkedHashMap resultMap){
        Map<String, Integer> columnMap = new HashMap<>();
        ArrayList columnList = (ArrayList) resultMap.get("columnList");
        //字段与index映射，key: 度量时格式为聚合函数（字段名）如sum（orderCount），value: search服务返回列的索引(int)
        for (int i = 0; i < columnList.size(); i++) {
            LinkedHashMap columnInfo = (LinkedHashMap) columnList.get(i);
            if(columnInfo != null){
                Integer index = (Integer) columnInfo.get("index");
                String name = (String) columnInfo.get("name");
                String fun = (String) columnInfo.get("aggType");
                if(index != null && StringUtils.isNotEmpty(name)){
                    if(StringUtils.isEmpty(fun)){
                        //维度没有聚合函数
                        columnMap.put(name, index);
                    }else {
                        columnMap.put(ExpressionUtils.getAggExpression(name, fun), index);
                    }
                }
            }
        }
        return columnMap;
    }

    //聚合查询结果集排序和过滤
    private void resultSortAndFilter(List<AttrReq> attrReqs , LinkedHashMap resultMap){
        if(resultMap != null && resultMap.get("data") != null){
            Map<String, Integer> columnMap = getColumnAndIndexMap(resultMap);
            Integer sortColumnIndex = -1;
            String sortType = "";
            Integer maximum = 0;
            for (AttrReq attrReq : attrReqs) {
                if (ArrayUtils.contains(WidgetConfigParser.SORT, attrReq.getFilterType()) &&
                        columnMap.get(attrReq.getColumnName()) != null) {
                    sortColumnIndex = columnMap.get(attrReq.getColumnName());
                    sortType = attrReq.getFilterType();
                    if (attrReq.getValues() != null && attrReq.getValues().size() > 0) {
                        maximum = Integer.parseInt(attrReq.getValues().get(0).getValue());
                    }
                    break;
                }
            }
            ArrayList data = (ArrayList) resultMap.get("data");
            List<ArrayList> fixedResult = new ArrayList();
            ArrayList tempList = new ArrayList();
            for (int i = 0; i < data.size(); i++) {
                ArrayList rowData = (ArrayList) data.get(i);
                if(filterResultRow(rowData, attrReqs, columnMap)){
                    //排序
                    if(sortColumnIndex != -1  && StringUtils.isNotEmpty(sortType)){
                        //不可排序的数据缓存起来，追加到末尾
                        if(rowData.size() < sortColumnIndex || rowData.get(sortColumnIndex) == null
                                || NumberValidationUtils.isNumber((String) rowData.get(sortColumnIndex))){
                            tempList.add(rowData);
                        }
                        if(CollectionUtils.isEmpty(fixedResult)){
                            fixedResult.add(rowData);
                        }else{
                            for (int j = 0; j < fixedResult.size(); j++) {
                                if(compare(sortType, (String) rowData.get(sortColumnIndex), (String) fixedResult.get(j).get(sortColumnIndex))){
                                    fixedResult.add(j, rowData);
                                    break;
                                }
                            }
                        }
                    }else{
                        fixedResult.add(rowData);
                    }
                }
            }
            fixedResult.addAll(tempList);
            if(maximum > 0 && fixedResult.size() > maximum){
                resultMap.put("data", fixedResult.stream()
                        .limit(maximum).collect(Collectors.toList()));
            }else {
                resultMap.put("data", fixedResult);
            }
        }
    }

    //根据排序方式，判断num1是否放在num2之前
    private boolean compare(String sortType, String num1, String num2){
        if(NumberValidationUtils.isNumber(num1) && NumberValidationUtils.isNumber(num2)){
            double value1 = Double.parseDouble(num1);
            double value2 = Double.parseDouble(num2);
            if(WidgetConfigParser.SORT[0].equalsIgnoreCase(sortType)){
                //top
                return value1 > value2;
            }else {
                //bottom
                return value1 < value2;
            }
        }else {
            if(WidgetConfigParser.SORT[0].equalsIgnoreCase(sortType)){
                return num1.compareTo(num2) > 0;
            }else{
                return num1.compareTo(num2) < 0;
            }
        }

    }

    //返回true 表示该行未被过滤掉
    private boolean filterResultRow(ArrayList rowData, List<AttrReq> attrReqs , Map<String, Integer> columnMap){
        for (AttrReq attrReq : attrReqs) {
            boolean filterResult = true;
            if(StringUtils.isNotEmpty(attrReq.getFilterType()) && StringUtils.isNotEmpty(attrReq.getColumnName())
                    && attrReq.getValues() != null && attrReq.getValues().size() > 0
                    && (! ArrayUtils.contains(WidgetConfigParser.SORT, attrReq.getFilterType()))){
                double value1 = Double.parseDouble(attrReq.getValues().get(0).getValue());
                double value2 = 0.0;
                if(attrReq.getValues().size() > 1){
                    value2 = Double.parseDouble(attrReq.getValues().get(1).getValue());
                }
                Integer columnKey = columnMap.get(attrReq.getColumnName());
                Object rowValueObj = rowData.get(columnKey);
                double rowValue = 0.0;
                if(rowValueObj != null && NumberValidationUtils.isNumber((String) rowValueObj)){
                    rowValue = Double.parseDouble((String) rowValueObj);
                }else {
                    continue;
                }
                switch (attrReq.getFilterType()) {
                    case "=":
                        filterResult = rowValue == value1;
                        break;
                    case "≠":
                        filterResult = rowValue != value1;
                        break;
                    case ">":
                        filterResult = rowValue > value1;
                        break;
                    case "<":
                        filterResult = rowValue < value1;
                        break;
                    case "≥":
                        filterResult = rowValue >= value1;
                        break;
                    case "≤":
                        filterResult = rowValue <= value1;
                        break;
                    case "(a,b]":
                        filterResult = rowValue > value1 && rowValue <= value2;
                        break;
                    case "[a,b)":
                        filterResult = rowValue >= value1 && rowValue < value2;
                        break;
                    case "(a,b)":
                        filterResult = rowValue > value1 && rowValue < value2;
                        break;
                    case "[a,b]":
                        filterResult = rowValue >= value1 && rowValue <= value2;
                        break;
                    default:
                }
                if(! filterResult){
                    return false;
                }
            }
        }
        return true;
    }
}
