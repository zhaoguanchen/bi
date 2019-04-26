package com.yiche.bigdata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.constants.DataSourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.DatasetDao;
import com.yiche.bigdata.dao.DatasourceDao;
import com.yiche.bigdata.dao.WidgetDao;
import com.yiche.bigdata.entity.dto.ApiResponse;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.pojo.TableDetail;
import com.yiche.bigdata.entity.pojo.WidgetDataSearchItem;
import com.yiche.bigdata.entity.vo.DashboardDatasetVo;
import com.yiche.bigdata.entity.vo.DashboardDatasourceVo;
import com.yiche.bigdata.service.DataManagerService;
import com.yiche.bigdata.service.MetaDataFeignService;
import com.yiche.bigdata.service.SearchServerFeignService;
import com.yiche.bigdata.service.core.UserComponentService;
import com.yiche.bigdata.service.core.AggSearchAfterProcessor;
import com.yiche.bigdata.service.core.WidgetConfigModel;
import com.yiche.bigdata.service.core.WidgetConfigParser;
import com.yiche.bigdata.utils.ExportExcelUtils;
import com.yiche.bigdata.utils.ResultUtils;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class DataManagerServiceImpl implements DataManagerService {

    @Autowired
    DatasourceDao datasourceDao;

    @Autowired
    DatasetDao datasetDao;

    @Autowired
    WidgetDao widgetDao;

    @Autowired
    SearchServerFeignService searchServerFeignService;

    @Autowired
    UserComponentService userComponentService;

    @Autowired
    MetaDataFeignService metaDataFeignService;

    @Autowired
    WidgetConfigParser widgetConfigParser;

    @Autowired
    AggSearchAfterProcessor widgetAfterProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataManagerServiceImpl.class);

    @Override
    public Result getColumns(String datasourceId, String query, Boolean reload) {
        if (StringUtils.isEmpty(datasourceId)||StringUtils.isEmpty(query)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Datasource datasource = datasourceDao.getDatasourceByResId(datasourceId);
        if (datasource==null) {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_NOT_EXIST);
        }
        if(StringUtils.isEmpty(query)){
            return ResultUtils.buildResult(ResultCode.DATASOURCE_PARAM_NOT_EXIST, "查询参数为空");
        }
        DashboardDatasourceVo dashboardDatasourceVo = new DashboardDatasourceVo();
        dashboardDatasourceVo.setConfig(datasource.getConfig());
        dashboardDatasourceVo.setType(DataSourceType.getByValue(datasource.getType()).getName());
        Object result = null;
        try{
            result = searchServerFeignService.getColumns(
                    JSONObject.toJSONString(dashboardDatasourceVo),
                    null,
                    query,
                    reload
            );
        }
        catch (FeignException exception){
            return handlerFeignException(exception);
        }
        if(result == null){
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_PARAM_NOT_EXIST);
        }else{
            if(((LinkedHashMap) result).get("msg") == null){
                return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_PARAM_EXCEPTION, JSONObject.toJSON(result));
            }
            else if("1".equals(((LinkedHashMap) result).get("msg"))){
                return ResultUtils.buildResult(ResultCode.OK, result);
            }else {
                Object msg = ((LinkedHashMap) result).get("msg");
                return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_PARAM_EXCEPTION, msg.toString());
            }
        }
    }

    private Result handlerFeignException(FeignException exception){
        LOGGER.error("search server exception", exception);
        if(exception.status() == 500){
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_ERROR);
        }else if(exception.getMessage().contains("Read timed out")){
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_TIME_OUT);
        }
        return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_EXCEPTION);
    }

    @Override
    public ResponseEntity<byte[]> downloadExcel(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return null;
        }
        Widget widget = widgetDao.getWidgetByResId(resId);
        if (widget==null) {
            return null;
        }
        Dataset dataset = datasetDao.getDatasetByResId(widget.getDatasetId());
        if (dataset==null) {
            return null;
        }
        DashboardDatasetVo dashboardDatasetVo = new DashboardDatasetVo();
        dashboardDatasetVo.setName(dataset.getName());
        dashboardDatasetVo.setData(dataset.getDataJson());
        JSONObject data = JSONObject.parseObject(dataset.getDataJson());
        Datasource datasource = datasourceDao.getDatasourceByResId(data.getString("datasource"));
        if (datasource==null) {
            return null;
        }
        DashboardDatasourceVo dashboardDatasourceVo = new DashboardDatasourceVo();
        dashboardDatasourceVo.setConfig(datasource.getConfig());
        dashboardDatasourceVo.setType(DataSourceType.getByValue(datasource.getType()).getName());
        Object result = searchServerFeignService.getAggregateData(
                JSONObject.toJSONString(dashboardDatasourceVo),
                JSONObject.toJSONString(dashboardDatasetVo),
                null,
                "{\"timePrimaryColumn\":\"/yiche_quote__app_leads_channel_day/dimen/pt\",\"values\":[],\"type\":\"\"}",
                false,
                false,
                "{\"rows\":[{\"columnName\":\"/yiche_quote__app_leads_channel_day/dimen/pt\",\"filterType\":\"=\",\"values\":[{\"value\":\"2016-01-01\",\"key\":\"2016-01-01\"},{\"value\":\"2016-01-02\",\"key\":\"2016-01-02\"},{\"value\":\"2016-01-03\",\"key\":\"2016-01-03\"},{\"value\":\"2016-01-04\",\"key\":\"2016-01-04\"},{\"value\":\"2016-01-06\",\"key\":\"2016-01-06\"},{\"value\":\"2016-01-07\",\"key\":\"2016-01-07\"},{\"value\":\"2016-01-08\",\"key\":\"2016-01-08\"}],\"id\":\"73f96cf1-3f50-444a-b643-f797fbcda62c\"}],\"columns\":[],\"filters\":[],\"values\":[{\"column\":\"/yiche_quote__app_leads_channel_day/metric/leads_cnt\",\"aggType\":\"sum\"}]}",
                false,
                null,
                JSONObject.toJSONString(userComponentService.getUserDateSetRowPermission(resId)));
        if (result == null) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(result));
        JSONArray column = jsonObject.getJSONArray("columnList");
        List<String> excelHeaders = new ArrayList<>();
        column.stream().forEach((json) -> excelHeaders.add(((JSONObject) json).getString("alias")));
        JSONArray excleBody = jsonObject.getJSONArray("data");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExportExcelUtils.exportExcel(excelHeaders, excleBody, out);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "table.xls");
        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.CREATED);
    }

    @Override
    public Result getAggregateData(String datasetId, String timeFilterString, Boolean hasLinkRatio, Boolean hasYoYRatio, String cfgString, Boolean reload) {
        if (StringUtils.isEmpty(datasetId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Dataset dataset = datasetDao.getDatasetByResId(datasetId);
        if (dataset==null) {
            return ResultUtils.buildResult(ResultCode.DATASET_NOT_EXIST);
        }
        DashboardDatasetVo dashboardDatasetVo = new DashboardDatasetVo();
        dashboardDatasetVo.setName(dataset.getName());
        dashboardDatasetVo.setData(dataset.getDataJson());
        JSONObject data = JSONObject.parseObject(dataset.getDataJson());
        Datasource datasource = datasourceDao.getDatasourceByResId(data.getString("datasource"));
        if (datasource==null) {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_NOT_EXIST);
        }
        DashboardDatasourceVo dashboardDatasourceVo = new DashboardDatasourceVo();
        dashboardDatasourceVo.setConfig(datasource.getConfig());
        dashboardDatasourceVo.setType(DataSourceType.getByValue(datasource.getType()).getName());
        Object result = null;
        try{
            result = searchServerFeignService.getAggregateData(
                JSONObject.toJSONString(dashboardDatasourceVo),
                JSONObject.toJSONString(dashboardDatasetVo),
                null,
                timeFilterString,
                hasLinkRatio,
                hasYoYRatio,
                cfgString,
                reload,
                null,
                JSONObject.toJSONString(userComponentService.getUserDateSetRowPermission(datasetId)));
        }catch (FeignException exception){
            return handlerFeignException(exception);
        }
        if(result==null){
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_PARAM_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, result);
    }


    @Override
    public Result getWidgetData(WidgetDataSearchItem dataSearchItem) {

        Boolean reload = false;
        JSONObject widget = getWidget(dataSearchItem);
        if(widget == null){
            return ResultUtils.buildResult(ResultCode.WIDGET_DATA_JSON_PARSE_ERROR);
        }

        String dataSetId = widget.getString("datasetId");
        if(StringUtils.isEmpty(dataSetId)){
            return ResultUtils.buildResult(ResultCode.DATASET_NOT_EXIST);
        }

        JSONObject config = widget.getJSONObject("config");

        Dataset dataset = datasetDao.getDatasetByResId(dataSetId);
        if(dataset ==  null){
            return ResultUtils.buildResult(ResultCode.DATASET_NOT_EXIST);
        }

        WidgetConfigModel model = widgetConfigParser.parse(config, dataset, dataSearchItem.getFilter());

        Result searchResult = getAggregateData(dataSetId, model.getTimeFilterString(),
                model.isHasLinkRatio(), model.isHasYoYRatio(), model.getSearchConfig(), reload);

        //查询结果修正
        if(searchResult.getCode() == ResultCode.OK.value()){
            widgetAfterProcessor.process(searchResult, model.getExpressions(), model.getAggFilters());
        }
        return searchResult;
    }


    private JSONObject getWidget(WidgetDataSearchItem dataSearchItem){
        String widgetString = null;
        if(StringUtils.isNotEmpty(dataSearchItem.getWidget_json())){
            widgetString = dataSearchItem.getWidget_json();
        }else {
            if(StringUtils.isNotEmpty(dataSearchItem.getWidgetId())){
                Widget widget = widgetDao.getWidgetByResId(dataSearchItem.getWidgetId());
                if(widget != null){
                    widgetString = widget.getDataJson();
                }
            }
        }
        if(StringUtils.isNotEmpty(widgetString)){
            try {
                JSONObject dataJson = JSONObject.parseObject(widgetString);
                return dataJson;
            }catch (JSONException exception){
                LOGGER.error("widget data json parse failure [{}] ... ", widgetString, exception);
            }
        }
        return null;
    }



    @Override
    public Result getDimensionValues(String query, String datasetId, String colmunName, String cfg, Boolean reload) {
        if (StringUtils.isEmpty(datasetId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Dataset dataset = datasetDao.getDatasetByResId(datasetId);
        if (dataset==null) {
            return ResultUtils.buildResult(ResultCode.DATASET_NOT_EXIST);
        }
        DashboardDatasetVo dashboardDatasetVo = new DashboardDatasetVo();
        dashboardDatasetVo.setName(dataset.getName());
        dashboardDatasetVo.setData(dataset.getDataJson());
        JSONObject data = JSONObject.parseObject(dataset.getDataJson());
        Datasource datasource = datasourceDao.getDatasourceByResId(data.getString("datasource"));
        if (datasource==null) {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_NOT_EXIST);
        }
        DashboardDatasourceVo dashboardDatasourceVo = new DashboardDatasourceVo();
        dashboardDatasourceVo.setConfig(datasource.getConfig());
        dashboardDatasourceVo.setType(DataSourceType.getByValue(datasource.getType()).getName());
        Object result = searchServerFeignService.getDimensionValues(
                JSONObject.toJSONString(dashboardDatasourceVo),
                JSONObject.toJSONString(dashboardDatasetVo),
                query, colmunName, cfg, reload);
        if(result==null){
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_PARAM_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, result);
    }

    @Override
    public Result testConnect(String type, String config, String query) {
        JSONObject queryO = JSONObject.parseObject(query);
        if (queryO.get("table") != null) {
            String tableName = queryO.get("table").toString();
            ApiResponse<TableDetail> apiResponse = metaDataFeignService.getTableDetail(tableName);
            if(apiResponse.getCode() == 0){
                TableDetail tableDetail = apiResponse.getData();
                if (tableDetail != null) {
                    String index = tableDetail.getIndex();
                    queryO.put("index", index + ".*");
                    queryO.put("type", tableName);
                }
            }
        }
        String msg = searchServerFeignService.testConnect(type, config, queryO.toString());
        if("success".equalsIgnoreCase(msg)){
            return ResultUtils.buildResult(ResultCode.OK);
        }else {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_CONNECT_FAILURE, msg);
        }
    }

}
