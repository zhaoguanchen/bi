package com.yiche.bigdata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.yiche.bigdata.constants.DataSourceType;
import com.yiche.bigdata.constants.PreviewType;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.DatasetDao;
import com.yiche.bigdata.dao.DatasourceDao;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.DataTable;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.generated.Permission;
import com.yiche.bigdata.entity.pojo.DatasourceItem;
import com.yiche.bigdata.entity.pojo.MetadataTableInfo;
import com.yiche.bigdata.entity.pojo.MetadataTableInfoOptions;
import com.yiche.bigdata.entity.pojo.NodeMappingInfo;
import com.yiche.bigdata.entity.vo.*;
import com.yiche.bigdata.service.DataSetPageService;
import com.yiche.bigdata.service.DataTableService;
import com.yiche.bigdata.service.SearchServerFeignService;
import com.yiche.bigdata.service.core.ResourceCenter;
import com.yiche.bigdata.service.core.UserComponentService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.DataSetColmunUtils;
import com.yiche.bigdata.utils.ExpressionUtils;
import com.yiche.bigdata.utils.ResultUtils;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.yiche.bigdata.constants.ExpressionType.Arithmetic;

@Service
public class DataSetPageServiceImpl extends AbstractUserResourceService implements DataSetPageService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetPageServiceImpl.class);
    @Autowired
    private ResourceCenter resourceCenter;

    @Autowired
    private UserComponentService userComponentService;

    @Autowired
    private DatasetDao datasetDao;

    @Autowired
    private DataTableService dataTableService;

    @Autowired
    DatasourceDao datasourceDao;

    @Autowired
    SearchServerFeignService searchServerFeignService;

    @Override
    public Result getDataSetCommonPermission(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Map<String, String> permissions = userComponentService.getBusinessLineLevelPermission(businessLine, ResourceType.DATA_SET);
        List<Permission> permissionList = resourceCenter.getResOpt().get(String.valueOf(ResourceType.DATA_SET.value()));
        for (Permission permission : permissionList) {
            if (!"view,download,drilldown".contains(permission.getKey())) {
                permissions.put(permission.getKey(), permission.getName());
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, permissions);
    }

    @Override
    public Result getDataSetSearchList(String businessLineId, String search) {
        if (StringUtils.isEmpty(businessLineId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (StringUtils.isEmpty(search)) {
            return getDataSetList(businessLineId);
        }
        Result result = getResourceList(businessLineId, "");
        List<CommonResourceVo> dataSetFolderList = (List<CommonResourceVo>) result.getResult();
        List<DataSetListVo> resultList = new ArrayList<>();

        for (CommonResourceVo folderItem : dataSetFolderList) {
            Boolean spread = false;
            DataSetListVo dataSetFolderItem = new DataSetListVo(folderItem);
            if (folderItem.getName().contains(search)) {
                List<DataSetListVo> dataSetList = new ArrayList<>();
                List<CommonResourceVo> dataSetListItem = (List<CommonResourceVo>) getResourceList(folderItem.getId(), "").getResult();
                for (CommonResourceVo item : dataSetListItem) {
                    if (item.getName().contains(search)) {
                        spread = true;
                    }
                    DataSetListVo dataSetItem = new DataSetListVo(item);
                    dataSetList.add(dataSetItem);
                }
                dataSetFolderItem.setChildren(dataSetList);
                dataSetFolderItem.setSpread(spread);
                resultList.add(dataSetFolderItem);
            } else {
                List<DataSetListVo> dataSetList = new ArrayList<>();
                List<CommonResourceVo> dataSetListItem = (List<CommonResourceVo>) getResourceList(folderItem.getId(), search).getResult();
                if (dataSetListItem.isEmpty()) {
                    continue;
                } else {
                    for (CommonResourceVo item : dataSetListItem) {
                        if (item.getName().contains(search)) {
                            spread = true;
                            DataSetListVo dataSetItem = new DataSetListVo(item);
                            dataSetList.add(dataSetItem);
                        }

                    }
                    dataSetFolderItem.setChildren(dataSetList);
                    dataSetFolderItem.setSpread(spread);
                    resultList.add(dataSetFolderItem);
                }

            }

        }

        return ResultUtils.buildResult(ResultCode.OK, resultList);
    }


    @Override
    public Result getDataSetList(String businessLineId) {

        String search = "";
        if (StringUtils.isEmpty(businessLineId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }

        Result result = getResourceList(businessLineId, search);
        List<CommonResourceVo> dataSetFolderList = (List<CommonResourceVo>) result.getResult();
        List<DataSetListVo> resultList = new ArrayList<>();
        for (CommonResourceVo folderItem : dataSetFolderList) {
            List<DataSetListVo> dataSetList = new ArrayList<>();
            List<CommonResourceVo> dataSetListItem = (List<CommonResourceVo>) getResourceList(folderItem.getId(), search).getResult();
            DataSetListVo dataSetItem = new DataSetListVo(folderItem);
            for (CommonResourceVo item : dataSetListItem) {
                DataSetListVo dataSetListChild = new DataSetListVo(item);
                dataSetList.add(dataSetListChild);
            }
            dataSetItem.setChildren(dataSetList);
            resultList.add(dataSetItem);
        }
        return ResultUtils.buildResult(ResultCode.OK, resultList);
    }


    @Override
    public Result getPagedDataSet(PagedQueryItem<Map> queryItem) {
        return getPagedResource(queryItem);
    }

    @Override
    protected void resourceVoPostProcessing(List<CommonResourceVo> voList) {
        for (CommonResourceVo commonResourceVo : voList) {
            commonResourceVo.getPermissions().remove("add_dataset");
            commonResourceVo.getPermissions().remove("drilldown");
            commonResourceVo.getPermissions().remove("download");
        }
    }

    @Override
    ResourceType[] getResourceType() {
        return new ResourceType[]{ResourceType.DATA_SET_DIRECTORY, ResourceType.DATA_SET};
    }

    @Override
    public Result listDataSource(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        return ResultUtils.buildResult(ResultCode.OK,
                getUserContext().getDataSourcePermission().get(businessLine));
    }


    @Override
    public Result<Map<String, List<DataTableSelectVo>>> listDataSourceTables(String businessLine, String datasourceId) {
        if (StringUtils.isEmpty(businessLine) && StringUtils.isEmpty(datasourceId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<DatasourceItem> datasourceItemList = getUserContext().getDataSourcePermission().get(businessLine);
        List<String> tableList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(datasourceItemList)) {
            for (DatasourceItem datasourceItem : datasourceItemList) {
                if (datasourceItem.getId().equals(datasourceId)) {
                    if (!CollectionUtils.isEmpty(datasourceItem.getTables())) {
                        for (Object object : datasourceItem.getTables()) {
                            if (object instanceof DataTable) {
                                DataTable table = (DataTable) object;
                                tableList.add(table.getTableName());
                            } else if (object instanceof String) {
                                tableList.add((String) object);
                            }
                        }
                    }
                }
            }
        }
        Map<String, List<DataTableSelectVo>> resultVo = new HashMap<>();
        Result<List<MetadataTableInfoOptions>> result = dataTableService.listDataSourceDataTables(datasourceId);
        if (result.getResult() != null) {
            for (MetadataTableInfoOptions tableInfoOptions : result.getResult()) {
                String key = tableInfoOptions.getTitle();
                List<DataTableSelectVo> tables = new ArrayList<>();
                for (MetadataTableInfo metadataTableInfo : tableInfoOptions.getList()) {
                    if (tableList.contains(metadataTableInfo.getTable())) {
                        DataTableSelectVo dataTableSelectVo = new DataTableSelectVo();
                        dataTableSelectVo.setName(metadataTableInfo.getName());
                        dataTableSelectVo.setTable(metadataTableInfo.getTable());
                        tables.add(dataTableSelectVo);
                    }
                }
                if (!CollectionUtils.isEmpty(tables)) {
                    resultVo.put(key, tables);
                }
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, resultVo);
    }

    @Override
    public Result<Map<String, List<DataTableSelectVo>>> listDataSourceTable(String businessLine, String datasourceId, String condition) {
        if (StringUtils.isEmpty(businessLine) && StringUtils.isEmpty(datasourceId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<DatasourceItem> datasourceItemList = getUserContext().getDataSourcePermission().get(businessLine);
        List<String> tableList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(datasourceItemList)) {
            for (DatasourceItem datasourceItem : datasourceItemList) {
                if (datasourceItem.getId().equals(datasourceId)) {
                    if (!CollectionUtils.isEmpty(datasourceItem.getTables())) {
                        for (Object object : datasourceItem.getTables()) {
                            if (object instanceof DataTable) {
                                DataTable table = (DataTable) object;
                                tableList.add(table.getTableName());
                            } else if (object instanceof String) {
                                tableList.add((String) object);
                            }
                        }
                    }
                }
            }
        }
        Map<String, List<DataTableSelectVo>> resultVo = new HashMap<>();
        Result<List<MetadataTableInfoOptions>> result = dataTableService.listDataSourceDataTables(datasourceId);
        if (result.getResult() != null) {
            for (MetadataTableInfoOptions tableInfoOptions : result.getResult()) {
                String key = tableInfoOptions.getTitle();
                List<DataTableSelectVo> tables = new ArrayList<>();
                for (MetadataTableInfo metadataTableInfo : tableInfoOptions.getList()) {
                    if (tableList.contains(metadataTableInfo.getTable())) {
                        if (!key.contains(condition)) {
                            if (metadataTableInfo.getTable().contains(condition)) {
                                DataTableSelectVo dataTableSelectVo = new DataTableSelectVo();
                                dataTableSelectVo.setName(metadataTableInfo.getName());
                                dataTableSelectVo.setTable(metadataTableInfo.getTable());
                                tables.add(dataTableSelectVo);
                            }

                        } else {
                            DataTableSelectVo dataTableSelectVo = new DataTableSelectVo();
                            dataTableSelectVo.setName(metadataTableInfo.getName());
                            dataTableSelectVo.setTable(metadataTableInfo.getTable());
                            tables.add(dataTableSelectVo);
                        }

                    }
                }
                if (!CollectionUtils.isEmpty(tables)) {
                    resultVo.put(key, tables);
                }
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, resultVo);
    }

    @Override
    public Result getDataSetSelector(String pid, String filter) {
        if (StringUtils.isEmpty(pid)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<ResourceSelectorVo> selectorVoList = userComponentService.listUserResourceSelectorVo(pid, ResourceType.DATA_SET.value(), filter);
        return ResultUtils.buildResult(ResultCode.OK, selectorVoList);
    }

    @Override
    public Result getDataSetColumns(List<String> dataSetIdList) {
        if (CollectionUtils.isEmpty(dataSetIdList)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<Dataset> datasetList = datasetDao.getDatasetByResIds(dataSetIdList);
        List<DataSetColumnsVo> dataSetColumnsVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(datasetList)) {
            for (Dataset dataset : datasetList) {
                DataSetColumnsVo dataSetColumnsVo = new DataSetColumnsVo();
                dataSetColumnsVo.setResId(dataset.getResId());
                dataSetColumnsVo.setName(dataset.getName());
                Map<String, NodeMappingInfo> allColumns = userComponentService.getTableColumns(dataset);
                if (allColumns == null) {
                    continue;
                }
                JSONObject dataSetJson = JSONObject.parseObject(dataset.getDataJson());
                JSONArray dataSetDimensions = dataSetJson.getJSONObject("schema").getJSONArray("dimension");

                List<TableColumnInfoVo> tableColumnInfoVoList = new ArrayList<>();

                for (int i = 0; i < dataSetDimensions.size(); i++) {
                    JSONObject dataSetDimension = (JSONObject) dataSetDimensions.get(i);
                    String column = dataSetDimension.getString("column");
                    if (allColumns.get(column) != null) {
                        NodeMappingInfo columnDetail = allColumns.get(column);
                        TableColumnInfoVo tableColumnInfoVo = new TableColumnInfoVo();
                        tableColumnInfoVo.setColumn(columnDetail.getKey());
                        tableColumnInfoVo.setName(columnDetail.getName());
                        tableColumnInfoVo.setType(DataSetColmunUtils.getTypeKey(columnDetail.getEs().getType()));
                        tableColumnInfoVo.setDisplay(DataSetColmunUtils.getDisplayName(columnDetail.getEs().getType(), columnDetail.getName()));
                        tableColumnInfoVo.setFormat(columnDetail.getEs().getFormat());
                        tableColumnInfoVoList.add(tableColumnInfoVo);
                    }
                }
                dataSetColumnsVo.setColumns(tableColumnInfoVoList);
                dataSetColumnsVoList.add(dataSetColumnsVo);
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, dataSetColumnsVoList);
    }

    @Override
    public Result viewDataSet(String dateSet, String type) {

        JSONObject data;


        if (type.equals(PreviewType.PREVIEW_BY_ID.value())) {
            Dataset dataset = datasetDao.getDatasetByResId(dateSet);
            if (dataset == null) {
                return ResultUtils.buildResult(ResultCode.DATASET_NOT_EXIST);
            }

            data = parseObject(dataset.getDataJson());

        } else {

            data = parseObject(dateSet);


        }

        JSONObject schemaJson = parseObject(JSONObject.toJSONString(data.get("schema")));
        JSONArray measureList = JSONObject.parseArray(CommonUtils.getString(schemaJson.get("measure")));
        JSONArray dimensionList = JSONObject.parseArray(CommonUtils.getString(schemaJson.get("dimension")));
        JSONArray expressionsList = JSONObject.parseArray(CommonUtils.getString(data.get("expressions")));
        List<ConfigRowsVo> configRowsVoList = new ArrayList<>();
        List<ConfigValuesVo> configValuesVoList = new ArrayList<>();
        if (measureList != null || measureList.size() > 0) {
            measureList.forEach(obj -> {
                JSONObject jsonObject = parseObject(obj.toString());
                configValuesVoList.add(new ConfigValuesVo(CommonUtils.getString(jsonObject.get("column")), addAgg(jsonObject.getString("columnType"))));
            });
        }
        if (expressionsList != null || expressionsList.size() > 0) {
            expressionsList.forEach(obj -> {
                JSONObject jsonObject = parseObject(obj.toString());
                configValuesVoList.add(new ConfigValuesVo(CommonUtils.getString(jsonObject.get("exp")), addAgg(jsonObject.getString("columnType"))));
            });
        }
        if (dimensionList != null || dimensionList.size() > 0) {
            dimensionList.forEach(obj -> {
                JSONObject jsonObject = parseObject(obj.toString());
                configRowsVoList.add(new ConfigRowsVo(getColumnByColumnObject(jsonObject.get("column")), "eq",
                        null, CommonUtils.getString(jsonObject.get("id"))));
            });
        }
        DashboardDatasetVo dashboardDatasetVo = new DashboardDatasetVo();
        dashboardDatasetVo.setData(data.toString());
        Datasource datasource = datasourceDao.getDatasourceByResId(data.getString("datasource"));
        if (datasource == null) {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_NOT_EXIST);
        }
        DashboardDatasourceVo dashboardDatasourceVo = new DashboardDatasourceVo();
        dashboardDatasourceVo.setConfig(datasource.getConfig());
        dashboardDatasourceVo.setType(DataSourceType.getByValue(datasource.getType()).getName());
        Object result = null;
        try {
            result = searchServerFeignService.getAggregateData(
                    JSONObject.toJSONString(dashboardDatasourceVo),
                    JSONObject.toJSONString(dashboardDatasetVo),
                    null,
                    null,
                    false,
                    false,
                    JSONObject.toJSONString(new ConfigVo(configRowsVoList, null, null, configValuesVoList)),
                    false,
                    null,
                    null
            );
        } catch (FeignException exception) {
            return handlerFeignException(exception);
        }
        if (result == null) {
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_PARAM_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, result);
    }


    private String getColumnByColumnObject(Object columnObject) {
        try {
            JSONObject column = JSONObject.parseObject(JSONObject.toJSONString(columnObject));
//            JSONObject.toJSONString(parseObject(CommonUtils.getString(columnObject)));
            return JSONObject.toJSONString(column.get("column"));
        } catch (Exception e) {
            return CommonUtils.getString(columnObject);
        }
    }

    private String addAgg(String columnType) {
        if (StringUtils.isEmpty(columnType)) {
            return "null";
        }
        if ("int".equalsIgnoreCase(columnType)) {
            return "sum";
        }
        return "count";
    }

    public Result addColumnTest(String column, String dataSourceId, String query, String type) {
        if (Arithmetic.value().equalsIgnoreCase(type)) {
            column = getFormatColumn(column);
        }
        if (StringUtils.isEmpty(column)) {
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_COLUMN_ERROR);
        }
        List<ConfigRowsVo> configRowsVoList = new ArrayList<>();
        List<ConfigValuesVo> configValuesVoList = new ArrayList<>();
        configValuesVoList.add(new ConfigValuesVo(column, addAgg(null)));
//        DashboardDatasetVo dashboardDatasetVo = new DashboardDatasetVo();
        Datasource datasource = datasourceDao.getDatasourceByResId(dataSourceId);
        if (datasource == null) {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_NOT_EXIST);
        }
        DashboardDatasourceVo dashboardDatasourceVo = new DashboardDatasourceVo();
        dashboardDatasourceVo.setConfig(datasource.getConfig());
        dashboardDatasourceVo.setType(DataSourceType.getByValue(datasource.getType()).getName());
        Object result = null;
        try {
            result = searchServerFeignService.addColumnTest(
                    JSONObject.toJSONString(dashboardDatasourceVo),
                    query,
                    JSONObject.toJSONString(new ConfigVo(configRowsVoList, null, null, configValuesVoList)),
                    false);
        } catch (FeignException exception) {
            return handlerFeignException(exception);
        }
        if (result == null) {
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_COLUMN_ERROR);
        }
        return ResultUtils.buildResult(ResultCode.OK);
    }


    private String getFormatColumn(String column) {
//        String column = "((uv/pve)*(pv+uv))+uv";
        if (StringUtils.isEmpty(column)) {
            return null;
        }
        char[] columnChars;
        columnChars = column.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char columnChar : columnChars) {
            if (columnChar == '+'
                    || columnChar == '('
                    || columnChar == ')'
                    || columnChar == '-'
                    || columnChar == '*'
                    || columnChar == '/') {
                sb.append(",");
            } else {
                sb.append(columnChar);
            }
        }
        String result = new String(sb);
        String[] res = result.split(",");
        List<String> columnList = new ArrayList<>();
        for (String value : res) {
            if (!StringUtils.isEmpty(value)) {
                columnList.add(value);
            }
        }
//        StringBuilder columnSb=new StringBuilder();
        StringJoiner columnsJoiner = new StringJoiner(", ", "", " ");
        columnList.forEach(item -> {
            columnsJoiner.add("sum(" + item + ")");
        });
        return columnsJoiner.toString();
    }


    public Result expressionValidator(ExpressionValidateVO expressionValidateVO) {

        String expressionStr = expressionValidateVO.getExpression();

        if (ExpressionUtils.isContainChinese(expressionStr)) {
            return ResultUtils.buildResult(ResultCode.EXPRESSION_CONTAINS_CHINESE_CHARACTER);
        }
        return addColumnTest(expressionValidateVO.getExpression(), expressionValidateVO.getDataSourceId(), expressionValidateVO.getQuery(), expressionValidateVO.getType());
    }


    private Result handlerFeignException(FeignException exception) {
        LOGGER.error("search server exception", exception);
        if (exception.status() == 500) {
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_ERROR);
        } else if (exception.getMessage().contains("Read timed out")) {
            return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_TIME_OUT);
        }
        return ResultUtils.buildResult(ResultCode.SEARCH_SERVER_EXCEPTION);
    }

}
