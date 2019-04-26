package com.yiche.bigdata.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yiche.bigdata.constants.DataSourceType;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.DataTableDao;
import com.yiche.bigdata.entity.dto.ApiResponse;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.DataTable;
import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.pojo.MetadataTableInfo;
import com.yiche.bigdata.entity.pojo.MetadataTableInfoOptions;
import com.yiche.bigdata.entity.pojo.TableDetail;
import com.yiche.bigdata.service.DataTableService;
import com.yiche.bigdata.service.DatasourceService;
import com.yiche.bigdata.service.MetaDataFeignService;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.ResultUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class DataTableServiceImpl implements DataTableService {

    private static final String EMPTYDATABASE = "empty_database";

    @Autowired
    private MetaDataFeignService metaDataFeignService;

    @Autowired
    private DataTableDao dataTableDao;

    @Autowired
    private ResourceTreeService resourceTreeService;

    @Autowired
    private DatasourceService datasourceService;


    @Override
    public Result<List<MetadataTableInfoOptions>> listDataSourceDataTables(String dataSourceId) {
        Result dataSourceResult = datasourceService.getDatasourceByResId(dataSourceId);
        if(dataSourceResult.getCode() == ResultCode.OK.value()){
            Datasource datasource = (Datasource) dataSourceResult.getResult();
            if(DataSourceType.ES_TYPE.value() == datasource.getType()){
                ApiResponse<List<MetadataTableInfo>> searchResult = metaDataFeignService.getAllAvailableTableInfo();
                if(searchResult.getCode() == 0){
                    List<MetadataTableInfoOptions> tableList = this.tidyTableOptionList(searchResult.getData());
                    return ResultUtils.buildResult(ResultCode.OK, tableList);
                }else{
                    return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
                }
            }
        }else{
           return dataSourceResult;
        }
        return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
    }

    @Override
    public Result listBusinessLineDataTables(String businessLine) {
        List<ResTree> dataTableNodes = resourceTreeService.listBusinessLineNodeByType(ResourceType.DATA_TABLE.value(), businessLine);
        if(CollectionUtils.isEmpty(dataTableNodes)){
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
        ApiResponse<List<MetadataTableInfo>> searchResult = metaDataFeignService.getAllAvailableTableInfo();
        if(searchResult.getCode() == 0){
            Map<String, DataTable> dataTableMap = new HashMap<>();
            dataTableNodes.stream().forEach((dataTableNode) -> {
                DataTable dataTable = dataTableDao.findDataTableById(dataTableNode.getId());
                dataTableMap.put(dataTable.getTableName(), dataTable);
            });
            List<MetadataTableInfo> businessLineDataTables = new ArrayList<>();
            searchResult.getData().stream().forEach((metadataTableInfo) -> {
                if(dataTableMap.keySet().contains(metadataTableInfo.getTable())){
                    metadataTableInfo.setResId(dataTableMap.get(metadataTableInfo.getTable()).getResId());
                    businessLineDataTables.add(metadataTableInfo);
                }
            });
            List<MetadataTableInfoOptions> tableList = this.tidyTableOptionList(businessLineDataTables);
            return ResultUtils.buildResult(ResultCode.OK, tableList);
        }else{
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
    }

    @Override
    public Result addDataTableDirectory(String name, String description, String pid, String businessLine) {
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(pid)){
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if(!resourceTreeService.checkNodeName(name, ResourceType.DATA_TABLE_DIRECTORY.value(), pid)){
            return ResultUtils.buildResult(ResultCode.DIRECTORY_NAME_REPEAT);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", name);
        param.put("pid", pid);
        param.put("type", ResourceType.DATA_TABLE_DIRECTORY.value());
        param.put("description", description);
        param.put("businessLine", businessLine);
        return resourceTreeService.addNode(param);
    }

    @Override
    public Result addDataTable(String dataSourceId, List<String> tableNames, String pid, String businessLine) {
        if(CollectionUtils.isEmpty(tableNames) || StringUtils.isEmpty(pid) || StringUtils.isEmpty(businessLine)){
            ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        for (String tableName: tableNames) {
            addDataTable(dataSourceId, tableName, pid, businessLine);
        }
        return ResultUtils.buildResult(ResultCode.OK);
    }

    @Override
    public Result addDataTable(String dataSourceId, String tableName, String pid, String businessLine) {
        if(StringUtils.isEmpty(tableName) || StringUtils.isEmpty(pid) || StringUtils.isEmpty(businessLine)){
            ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ApiResponse<TableDetail> apiResponse = metaDataFeignService.getTableDetail(tableName);
        if(apiResponse.getCode() == 0){
            TableDetail tableDetail = apiResponse.getData();
            if(StringUtils.isEmpty(tableDetail.getDatabase())){
                tableDetail.setDatabase(EMPTYDATABASE);
            }
            DataTable dataTable = new DataTable();
            String resId = CommonUtils.getUUID();
            dataTable.setResId(resId);
            dataTable.setDatasourceId(dataSourceId);
            dataTable.setName(tableDetail.getName());
            dataTable.setTableName(tableDetail.getTable());
            dataTable.setDatabaseName(tableDetail.getDatabase());
            if(! dataTableDao.addDataTable(dataTable)){
                return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
            }
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("resId", dataTable.getResId());
            param.put("name", tableDetail.getName());
            param.put("pid", pid);
            param.put("type", ResourceType.DATA_TABLE.value());
            param.put("businessLine", businessLine);
            return resourceTreeService.addNode(param);
        }else{
            return ResultUtils.buildResult(ResultCode.DATA_TABLE_NOT_EXIST);
        }
    }

    @Override
    public Result deleteDataTable(List<String> tableKey, String pid) {
        if(CollectionUtils.isEmpty(tableKey) || StringUtils.isEmpty(pid)){
            ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<ResTree> nodeList = resourceTreeService.listChildrenNodesByType(ResourceType.DATA_TABLE.value(), pid);
        if(CollectionUtils.isEmpty(nodeList)){
            return ResultUtils.buildResult(ResultCode.DATA_TABLE_NOT_EXIST);
        }
        List<String> nodeIdList = new ArrayList<>();
        nodeList.forEach(node -> nodeIdList.add(node.getId()));
        List<DataTable> dataTableList = dataTableDao.findByIds(nodeIdList);
        List<String> errorList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(dataTableList)){
            for (DataTable dataTable : dataTableList){
                if(tableKey.contains(dataTable.getDatasourceId() + "@@" + dataTable.getTableName())){
                    Result result = resourceTreeService.deleteNodeById(dataTable.getResId());
                    if(result.getCode() == ResultCode.OK.value()){
                        if(! dataTableDao.deleteById(dataTable.getResId())){
                            errorList.add(dataTable.getTableName());
                        }
                    }else {
                        errorList.add(dataTable.getTableName());
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(errorList)){
            return  ResultUtils.buildResult(ResultCode.OK);
        }else{
            return  ResultUtils.buildResult(ResultCode.SOME_DATA_RESOURCE_ASSIGNED_FAILURE, CommonUtils.listToString(errorList));
        }
    }

    @Override
    public Result getMetadataTableInfo(String tableName) {
        if(StringUtils.isEmpty(tableName)){
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ApiResponse<TableDetail> apiResponse = metaDataFeignService.getTableDetail(tableName);
        if(apiResponse.getCode() == 0) {
            TableDetail tableDetail = apiResponse.getData();
            if (StringUtils.isEmpty(tableDetail.getDatabase())) {
                tableDetail.setDatabase(EMPTYDATABASE);
            }
            return ResultUtils.buildResult(ResultCode.OK, tableDetail);
        }else{
            return ResultUtils.buildResult(ResultCode.LOAD_METADATA_FAILURE);
        }
    }

    @Override
    public Result getMetadataTableInfoById(String resId) {
        if(StringUtils.isEmpty(resId)){
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        DataTable dataTable = dataTableDao.findDataTableById(resId);
        if(dataTable == null){
            return ResultUtils.buildResult(ResultCode.LOAD_DATA_TABLE_FAILURE);
        }else {
            return getMetadataTableInfo(dataTable.getTableName());
        }
    }

    @Override
    public Result deleteDataTableById(String resId) {
        Result result = resourceTreeService.deleteNodeById(resId);
        if(result.getCode() == ResultCode.OK.value()){
            if(!dataTableDao.deleteById(resId)){
                return ResultUtils.buildResult(ResultCode.DELETE_DATA_TABLE_FAILURE);
            }
        }
        return result;
    }

    private List<MetadataTableInfoOptions> tidyTableOptionList(List<MetadataTableInfo> allMetadataList) {
        List<MetadataTableInfoOptions> resultList = Lists.newArrayList();
        Map<String, List<MetadataTableInfo>> resultMap = Maps.newHashMap();
        if (allMetadataList != null && !allMetadataList.isEmpty()) {
            for (MetadataTableInfo metadataTableInfo : allMetadataList) {
                if (metadataTableInfo.getDatabase() == null) {
                    if (resultMap.containsKey(EMPTYDATABASE)) {
                        resultMap.get(EMPTYDATABASE).add(metadataTableInfo);
                    } else {
                        List<MetadataTableInfo> metadataTableInfoList = Lists.newArrayList();
                        metadataTableInfoList.add(metadataTableInfo);
                        resultMap.put(EMPTYDATABASE, metadataTableInfoList);
                    }
                } else if (resultMap.containsKey(metadataTableInfo.getDatabase())) {
                    resultMap.get(metadataTableInfo.getDatabase()).add(metadataTableInfo);
                } else {
                    List<MetadataTableInfo> metadataTableInfoList = Lists.newArrayList();
                    metadataTableInfoList.add(metadataTableInfo);
                    resultMap.put(metadataTableInfo.getDatabase(), metadataTableInfoList);
                }
            }
        }
        if (!resultMap.isEmpty()) {
            Set<String> keySet = resultMap.keySet();
            List<String> keyList = Lists.newArrayList();
            keyList.addAll(keySet);
            Collections.sort(keyList);
            for (String key : keyList) {
                Collections.sort(resultMap.get(key));
                resultList.add(new MetadataTableInfoOptions(key, resultMap.get(key).toArray(new MetadataTableInfo[0])));
            }
        }
        return resultList;
    }

}
