package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.Constant;
import com.yiche.bigdata.constants.DataSourceType;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.BusinessLineUserDao;
import com.yiche.bigdata.dao.DataTableDao;
import com.yiche.bigdata.dao.DatasourceDao;
import com.yiche.bigdata.dao.DatasourcePermissionDao;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Pagination;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.DataTable;
import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.generated.DatasourcePermission;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.pojo.BusinessLineItem;
import com.yiche.bigdata.entity.pojo.DatasourceItem;
import com.yiche.bigdata.entity.pojo.MetadataTableInfoOptions;
import com.yiche.bigdata.entity.vo.BusinessLineVo;
import com.yiche.bigdata.entity.vo.DataSourceChooseVo;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.service.*;
import com.yiche.bigdata.service.core.UserContextContainer;
import com.yiche.bigdata.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class BusinessLineServiceImpl implements BusinessLineService {

    @Autowired
    private ResourceTreeService resourceTreeService;

    @Autowired
    private BusinessLineUserDao businessLineUserDao;

    @Autowired
    private WidgetServiceImpl widgetService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DataTableService dataTableService;

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DatasourcePermissionDao datasourcePermissionDao;

    @Autowired
    private DataTableDao dataTableDao;

    @Autowired
    private UserContextContainer userContextContainer;

    private static final String ASSIGN_DATA_TABLE_FAILURE = "assign_data_table_failure";

    private static final String ASSIGN_DATA_SOURCE_FAILURE = "assign_data_source_failure";

    private static final String REMOVE_DATA_TABLE_FAILURE = "remove_data_table_failure";

    private static final String REMOVE_DATA_SOURCE_FAILURE = "remove_data_source_failure";

    @Override
    public Result addBusinessLine(BusinessLineItem<String> businessLineItem) {
        if (StringUtils.isEmpty(businessLineItem.getName())) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!resourceTreeService.checkNodeName(businessLineItem.getName(), ResourceType.BUSINESS_LINE.value())) {
            return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_EXIST);
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", businessLineItem.getName());
        param.put("pid", "0");
        param.put("type", ResourceType.BUSINESS_LINE.value());
        param.put("description", businessLineItem.getDescription());
        Result result = resourceTreeService.addNode(param);
        if (result.getCode() == ResultCode.OK.value()) {
            ResTree businessLineNode = resourceTreeService.getNodeTypeAndName(businessLineItem.getName(), ResourceType.BUSINESS_LINE.value());
            businessLineItem.setId(businessLineNode.getId());
            result = businessLineDataInit(businessLineItem.getName());
            if (result.getCode() == ResultCode.OK.value()) {
                result = addBusinessLineResource(businessLineItem.getId(), businessLineItem.getDatasource());
            }
        }
        return result;
    }

    private Result addBusinessLineResource(String businessLine, List<DatasourceItem<String>> datasourceItemList) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (CollectionUtils.isEmpty(datasourceItemList)) {
            ResultUtils.buildResult(ResultCode.BUSINESS_LINE_DATASOURCE_EMPTY);
        }
        for (DatasourceItem<String> datasourceItem : datasourceItemList) {
            if (DataSourceType.ES_TYPE.getName().equals(datasourceItem.getType())) {
                for (String table : datasourceItem.getTables()) {
                    Result result = dataTableService.addDataTable(datasourceItem.getId(), table, businessLine, businessLine);
                    addEsDataSourceToBusinessLine(businessLine, datasourceItem.getId());
                    if (result.getCode() != ResultCode.OK.value()) {
                        errorMap.computeIfAbsent(ASSIGN_DATA_TABLE_FAILURE, key -> new ArrayList<>())
                                .add(table);
                    }
                }

            } else {
                Result result = addDataSourceToBusinessLine(businessLine, datasourceItem);
                if (result.getCode() != ResultCode.OK.value()) {
                    errorMap.computeIfAbsent(ASSIGN_DATA_SOURCE_FAILURE, key -> new ArrayList<>())
                            .add(datasourceItem.getName());
                }
            }
        }
        return buildAssignResourceResult(errorMap);
    }

    private Result buildAssignResourceResult(Map<String, List<String>> errorMap) {
        if (CollectionUtils.isEmpty(errorMap)) {
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            StringBuilder errorString = new StringBuilder();
            errorString.append("以下表保存出现异常：");
            if (!CollectionUtils.isEmpty(errorMap.get(ASSIGN_DATA_TABLE_FAILURE))) {
                errorString.append(CommonUtils.listToString(errorMap.get(ASSIGN_DATA_TABLE_FAILURE)));
            }
            if (!CollectionUtils.isEmpty(errorMap.get(ASSIGN_DATA_SOURCE_FAILURE))) {
                errorString.append(CommonUtils.listToString(errorMap.get(ASSIGN_DATA_SOURCE_FAILURE)));
            }
            if (!CollectionUtils.isEmpty(errorMap.get(REMOVE_DATA_TABLE_FAILURE))) {
                errorString.append(CommonUtils.listToString(errorMap.get(REMOVE_DATA_TABLE_FAILURE)));
            }
            if (!CollectionUtils.isEmpty(errorMap.get(REMOVE_DATA_SOURCE_FAILURE))) {
                errorString.append(CommonUtils.listToString(errorMap.get(REMOVE_DATA_SOURCE_FAILURE)));
            }
            return ResultUtils.buildResult(ResultCode.SOME_DATA_RESOURCE_ASSIGNED_FAILURE, errorString);
        }
    }

    private Result addDataSourceToBusinessLine(String businessLine, DatasourceItem datasourceItem) {
        DatasourcePermission datasourcePermission = new DatasourcePermission();
        datasourcePermission.setBusinessLine(businessLine);
        datasourcePermission.setDatasourceId(datasourceItem.getId());
        datasourcePermission.setDatasourceType(EnumUtils.nameOf(DataSourceType.class, datasourceItem.getType()).value());
        String user = userContextContainer.getUserContext(TokenUtils.getToken()).getUserId();
        datasourcePermission.setCreater(user);
        datasourcePermission.setLastModifier(user);
        if (datasourcePermissionDao.save(datasourcePermission)) {
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    private Result addEsDataSourceToBusinessLine(String businessLine, String datasourceId) {
        if (datasourcePermissionDao.findOne(datasourceId, businessLine) != null) {
            return ResultUtils.buildResult(ResultCode.OK);
        }
        DatasourcePermission datasourcePermission = new DatasourcePermission();
        datasourcePermission.setBusinessLine(businessLine);
        datasourcePermission.setDatasourceId(datasourceId);
        datasourcePermission.setDatasourceType(DataSourceType.ES_TYPE.value());
        String user = userContextContainer.getUserContext(TokenUtils.getToken()).getUserId();
        datasourcePermission.setCreater(user);
        datasourcePermission.setLastModifier(user);
        if (datasourcePermissionDao.save(datasourcePermission)) {
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    private Result businessLineDataInit(String name) {
        ResTree businessLineNode = getBusinessLineByName(name).getResult();
        //为业务线创建管理员角色
        Result result = roleService.addBusinessLineAdminRole(businessLineNode.getId(), name);
        if (result.getCode() != ResultCode.OK.value()) {
            return result;
        }

        final String defaultDirectoryName = Constant.DEFAULT_DIRECTORY.getStringName();
        //创建默认业务线图表目录
        DirectoryVO widgetDirectoryVO = new DirectoryVO();
        widgetDirectoryVO.setName(defaultDirectoryName);
        widgetDirectoryVO.setDescription(defaultDirectoryName);
        widgetDirectoryVO.setPid(businessLineNode.getId());
        widgetDirectoryVO.setBusinessLine(businessLineNode.getId());
        widgetService.addWidgetDirectory(widgetDirectoryVO);

        //创建默认业务线报表目录
        DirectoryVO reportDirectoryVO = new DirectoryVO();
        reportDirectoryVO.setName(defaultDirectoryName);
        reportDirectoryVO.setDescription(defaultDirectoryName);
        reportDirectoryVO.setPid(businessLineNode.getId());
        reportDirectoryVO.setBusinessLine(businessLineNode.getId());
        reportService.addReportDirectory(reportDirectoryVO);

        //创建默认业务数据集目录
        DirectoryVO datasetDirectoryVO = new DirectoryVO();
        datasetDirectoryVO.setName(defaultDirectoryName);
        datasetDirectoryVO.setDescription(defaultDirectoryName);
        datasetDirectoryVO.setPid(businessLineNode.getId());
        datasetDirectoryVO.setBusinessLine(businessLineNode.getId());
        datasetService.addDataSetDirectory(datasetDirectoryVO);

        return ResultUtils.buildResult(ResultCode.OK);
    }

    @Override
    public Result<ResTree> getBusinessLineByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree node = resourceTreeService.getNodeTypeAndName(name, ResourceType.BUSINESS_LINE.value());
        if (node == null) {
            return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_NOT_EXIST);
        } else {
            return ResultUtils.buildResult(ResultCode.OK, node);
        }
    }

    @Override
    public Result listBusinessLine() {
        List<ResTree> list = resourceTreeService.listAllNodeByType(ResourceType.BUSINESS_LINE.value());
        List<BusinessLineVo> voList = new ArrayList<>();
        list.stream().forEach((node) -> {
            BusinessLineVo vo = new BusinessLineVo();
            BeanUtils.copyProperties(node, vo);
            if (CollectionUtils.isEmpty(voList)) {
                voList.add(vo);
            } else {
                //按创建时间降序排列
                boolean addflag = false;
                for (int i = 0; i < voList.size(); i++) {
                    if (vo.getCreateTime() != null && voList.get(i).getCreateTime() != null) {
                        if (vo.getCreateTime().after(voList.get(i).getCreateTime())) {
                            voList.add(i, vo);
                            addflag = true;
                            break;
                        }
                    }
                }
                if (!addflag) {
                    voList.add(vo);
                }
            }
        });
        return ResultUtils.buildResult(ResultCode.OK, voList);
    }

    @Override
    public Result listBusinessLinePaged(PagedQueryItem<Map> queryItem) {
        Result allResult = listBusinessLine();
        if (allResult.getCode() == ResultCode.OK.value()) {
            List<BusinessLineVo> voList = (List<BusinessLineVo>) allResult.getResult();
            int start = PaginationUtil.startValue(queryItem.getPageNo(), queryItem.getPageSize());
            int end = PaginationUtil.endValue(queryItem.getPageNo(), queryItem.getPageSize());
            int totalPage = PaginationUtil.totalPage(voList.size(), queryItem.getPageSize());
            List<BusinessLineVo> pagedVoList = new ArrayList<>();
            if (end >= voList.size()) {
                end = voList.size() - 1;
            }
            if (start < voList.size()) {
                pagedVoList = voList.subList(start, end + 1);
            }
            Pagination<BusinessLineVo> resultVo = new Pagination<>(voList.size(), queryItem.getPageNo(), totalPage, pagedVoList);
            return ResultUtils.buildResult(ResultCode.OK, resultVo);

        } else {
            return allResult;
        }
    }

    @Override
    public Result getBusinessLineById(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree node = resourceTreeService.getNodeById(businessLine);
        if (node == null) {
            return ResultUtils.buildResult(ResultCode.NODE_NOT_EXIST);
        } else {
            BusinessLineVo vo = new BusinessLineVo();
            BeanUtils.copyProperties(node, vo);
            return ResultUtils.buildResult(ResultCode.OK, vo);
        }
    }

    @Override
    public Result getBusinessLineDetail(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree businessLineNode = resourceTreeService.getNodeById(businessLine);
        BusinessLineItem businessLineItem = new BusinessLineItem();
        BeanUtils.copyProperties(businessLineNode, businessLineItem);
        businessLineItem.setDatasource(getBusinessLineDataSource(businessLine));
        return ResultUtils.buildResult(ResultCode.OK, businessLineItem);
    }


    @Override
    public List<DatasourceItem> getBusinessLineDataSource(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return new ArrayList<DatasourceItem>();
        }
        List<DatasourceItem> datasourceItemList = new ArrayList<>();
        List<ResTree> dataTableNodeList = resourceTreeService
                .listChildrenNodesByType(ResourceType.DATA_TABLE.value(), businessLine);
        Map<String, Datasource> datasourceMap = getDatasourceMap();
        //查询es的事实表
        if (!CollectionUtils.isEmpty(dataTableNodeList)) {
            List<String> dataTableIdList = new ArrayList<>();
            dataTableNodeList.forEach(node -> dataTableIdList.add(node.getId()));
            List<DataTable> dataTables = dataTableDao.findByIds(dataTableIdList);
            if (!CollectionUtils.isEmpty(dataTables)) {
                Map<String, DatasourceItem<DataTable>> datasourceItemMap = new HashMap<>();
                for (DataTable dataTable : dataTables) {
                    DatasourceItem<DataTable> datasourceItem = datasourceItemMap.get(dataTable.getDatasourceId());
                    if (datasourceItem == null) {
                        Datasource datasource = datasourceMap.get(dataTable.getDatasourceId());
                        if (datasource != null) {
                            datasourceItem = new DatasourceItem();
                            datasourceItem.setId(dataTable.getDatasourceId());
                            datasourceItem.setName(datasource.getName());
                            datasourceItem.setType(EnumUtils.valueOf(DataSourceType.class, datasource.getType()).getName());
                            List<DataTable> temp = new ArrayList<>();
                            temp.add(dataTable);
                            datasourceItem.setTables(temp);
                            datasourceItemMap.put(dataTable.getDatasourceId(), datasourceItem);
                        }
                    } else {
                        datasourceItem.getTables().add(dataTable);
                    }
                }
                datasourceItemList.addAll(datasourceItemMap.values());
            }
        }

        //查询mysql
        getDataBaseDataSource(businessLine, datasourceItemList, datasourceMap);
        getKylinDataSource(businessLine, datasourceItemList, datasourceMap);
        return datasourceItemList;
    }

    private void getDataBaseDataSource(String businessLine, List<DatasourceItem> datasourceItemList, Map<String, Datasource> datasourceMap) {
        List<DatasourcePermission> datasourcePermissionList = datasourcePermissionDao.findByBusinessLine(businessLine);
        if (!CollectionUtils.isEmpty(datasourcePermissionList)) {
            for (DatasourcePermission datasourcePermission : datasourcePermissionList) {
                Datasource datasource = datasourceMap.get(datasourcePermission.getDatasourceId());
                if (datasource != null) {
                    if (datasource.getType() == DataSourceType.JDBC_TYPE.value()) {
                        DatasourceItem datasourceItem = new DatasourceItem();
                        datasourceItem.setId(datasourcePermission.getDatasourceId());
                        datasourceItem.setName(datasource.getName());
                        datasourceItem.setType(EnumUtils.valueOf(DataSourceType.class, datasource.getType()).getName());
                        datasourceItemList.add(datasourceItem);
                    }
                }
            }
        }
    }

    private void getKylinDataSource(String businessLine, List<DatasourceItem> datasourceItemList, Map<String, Datasource> datasourceMap) {
        List<DatasourcePermission> datasourcePermissionList = datasourcePermissionDao.findByBusinessLine(businessLine);
        if (!CollectionUtils.isEmpty(datasourcePermissionList)) {
            for (DatasourcePermission datasourcePermission : datasourcePermissionList) {
                Datasource datasource = datasourceMap.get(datasourcePermission.getDatasourceId());
                if (datasource != null) {
                    if (datasource.getType() == DataSourceType.KYLIN_TYPE.value()) {
                        DatasourceItem datasourceItem = new DatasourceItem();
                        datasourceItem.setId(datasourcePermission.getDatasourceId());
                        datasourceItem.setName(datasource.getName());
                        datasourceItem.setType(EnumUtils.valueOf(DataSourceType.class, datasource.getType()).getName());
                        datasourceItemList.add(datasourceItem);
                    }
                }
            }
        }
    }


    private Map<String, Datasource> getDatasourceMap() {
        Map<String, Datasource> datasourceMap = new HashMap<>();
        List<Datasource> datasourceList = datasourceDao.getDatasourceList();
        if (!CollectionUtils.isEmpty(datasourceList)) {
            for (Datasource datasource : datasourceList) {
                datasourceMap.put(datasource.getId(), datasource);
            }
        }
        return datasourceMap;
    }

    @Override
    public Result updateBusinessLine(BusinessLineItem businessLineItem) {
        if (businessLineItem == null || StringUtils.isEmpty(businessLineItem.getName())) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        ResTree resTree = resourceTreeService.getNodeById(businessLineItem.getId());
        if (resTree == null) {
            return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_NOT_EXIST);
        }
        if (!resourceTreeService.checkNodeName(businessLineItem.getName(), ResourceType.BUSINESS_LINE.value())
                && !resTree.getName().equals(businessLineItem.getName())) {
            return ResultUtils.buildResult(ResultCode.BUSINESS_LINE_EXIST);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", businessLineItem.getName());
        param.put("description", businessLineItem.getDescription());
        Result result = resourceTreeService.updateNode(businessLineItem.getId(), param);
        if (result.getCode() == ResultCode.OK.value()) {
            return updateBusinessLineResource(businessLineItem);
        }
        return result;
    }

    private Result updateBusinessLineResource(BusinessLineItem businessLineItem) {
        Result searchResult = getBusinessLineDetail(businessLineItem.getId());
        if (searchResult.getCode() != ResultCode.OK.value()) {
            return searchResult;
        }
        if (searchResult.getResult() != null) {
            //获得数据库中的mysql数据源和事实表
            Map<String, List<String>> errorMap = new HashMap<>();
            List<DatasourceItem> oldDatasourceItemList = ((BusinessLineItem) searchResult.getResult()).getDatasource();
            List<String> oldDataSourceIdList = new ArrayList<>();
            List<String> oldDataTableList = new ArrayList<>();
            getResourceList(oldDatasourceItemList, oldDataSourceIdList, oldDataTableList);

            List<DatasourceItem> datasourceItemList = businessLineItem.getDatasource();
            List<String> dataSourceIdList = new ArrayList<>();
            List<String> dataTableList = new ArrayList<>();
            getResourceList(datasourceItemList, dataSourceIdList, dataTableList);

            //对比添加mysql数据源
            List<String> newDataSourceList = new ArrayList<>();
            newDataSourceList.addAll(dataSourceIdList);
            newDataSourceList.removeAll(oldDataSourceIdList);
            if (!CollectionUtils.isEmpty(newDataSourceList)) {
                for (DatasourceItem dataSource : datasourceItemList) {
                    if (newDataSourceList.contains(dataSource.getId())) {
                        Result result = addDataSourceToBusinessLine(businessLineItem.getId(), dataSource);
                        if (result.getCode() != ResultCode.OK.value()) {
                            errorMap.computeIfAbsent(ASSIGN_DATA_SOURCE_FAILURE, key -> new ArrayList<>())
                                    .add(dataSource.getName());
                        }
                    }
                }
            }

            //对比删除mysql数据源
            List<String> removeDataSourceList = new ArrayList<>();
            removeDataSourceList.addAll(oldDataSourceIdList);
            removeDataSourceList.removeAll(dataSourceIdList);
            if (!CollectionUtils.isEmpty(removeDataSourceList)) {
                for (String dataSourceId : removeDataSourceList) {
                    if (!datasourcePermissionDao.delete(dataSourceId, businessLineItem.getId())) {
                        errorMap.computeIfAbsent(REMOVE_DATA_SOURCE_FAILURE, key -> new ArrayList<>())
                                .add(dataSourceId);
                    }
                }
            }

            //对比添加事实表
            List<String> newDataTableList = new ArrayList<>();
            newDataTableList.addAll(dataTableList);
            newDataTableList.removeAll(oldDataTableList);
            if (!CollectionUtils.isEmpty(newDataTableList)) {
                for (String key : newDataTableList) {
                    String[] args = key.split("@@");
                    Result result = dataTableService.addDataTable(args[0], args[1], businessLineItem.getId(), businessLineItem.getId());
                    addEsDataSourceToBusinessLine(businessLineItem.getId(), args[0]);
                    if (result.getCode() != ResultCode.OK.value()) {
                        errorMap.computeIfAbsent(ASSIGN_DATA_TABLE_FAILURE, table -> new ArrayList<>())
                                .add(args[1]);
                    }
                }
            }
            //对比删除事实表
            List<String> removeDataTableList = new ArrayList<>();
            removeDataTableList.addAll(oldDataTableList);
            removeDataTableList.removeAll(dataTableList);
            if (!CollectionUtils.isEmpty(removeDataTableList)) {
                Result result = dataTableService.deleteDataTable(removeDataTableList, businessLineItem.getId());
                if (result.getCode() != ResultCode.OK.value()) {
                    errorMap.computeIfAbsent(REMOVE_DATA_TABLE_FAILURE, table -> new ArrayList<>())
                            .addAll(CommonUtils.stringToList(result.getMessage()));
                }
            }
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
    }

    private void getResourceList(List<DatasourceItem> datasourceItemList, List<String> dataSourceIdList, List<String> dataTableList) {
        if (!CollectionUtils.isEmpty(datasourceItemList)) {
            for (DatasourceItem datasourceItem : datasourceItemList) {
                if (datasourceItem.getType().equals(DataSourceType.ES_TYPE.getName())) {
                    for (Object table : datasourceItem.getTables()) {
                        if (table instanceof DataTable) {
                            dataTableList.add(datasourceItem.getId() + "@@" + ((DataTable) table).getTableName());
                        } else {
                            dataTableList.add(datasourceItem.getId() + "@@" + table.toString());
                        }
                    }
                } else {
                    dataSourceIdList.add(datasourceItem.getId());
                }
            }
        }
    }

    @Override
    public Result deleteBusinessLine(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        return resourceTreeService.deleteNodeById(businessLine);
    }

    @Override
    public Result deleteBusinessLineForce(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<ResTree> children = resourceTreeService.listChildren(businessLine);
        boolean deleteAble = true;
        for (ResTree node : children) {
            if (!ResourceType.directoryTypes().contains(Integer.toString(node.getType()))) {
                deleteAble = false;
            }
        }
        if (!deleteAble) {
            return ResultUtils.buildResult(ResultCode.DELETE_NODE_FAILURE_CHILD_EXIST, "删除失败，请先删除业务线下资源！");
        } else {
            for (ResTree node : children) {
                if (ResourceType.directoryTypes().contains(Integer.toString(node.getType()))) {
                    resourceTreeService.deleteNodeById(node.getId());
                }
            }
            return resourceTreeService.deleteNodeById(businessLine);
        }
    }

    @Override
    public Result<String> getAllDepartment(String businessLine) {
        if (StringUtils.isEmpty(businessLine)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<String> result = businessLineUserDao.getAllDepartment(businessLine);
        if (CollectionUtils.isEmpty(result)) {
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
        Set<String> set = new LinkedHashSet<>();
        set.add("不限(一级部门)");
        result.stream().forEach((department) -> {
            if (StringUtils.isNotEmpty(department)) {
                set.add(department.split("-")[1]);
            }
        });
        return ResultUtils.buildResult(ResultCode.OK, set);
    }

    @Override
    public Result listAllDataSource() {
        List<Datasource> datasourceList = datasourceDao.getDatasourceList();
        if (CollectionUtils.isEmpty(datasourceList)) {
            return ResultUtils.buildResult(ResultCode.NO_DATASOURCE_EXIST);
        }
        List<DataSourceChooseVo<MetadataTableInfoOptions>> resultList = new ArrayList<>();
        for (Datasource datasource : datasourceList) {
            DataSourceChooseVo<MetadataTableInfoOptions> dataSourceChooseVo = new DataSourceChooseVo<>();
            BeanUtils.copyProperties(datasource, dataSourceChooseVo);
            DataSourceType type = EnumUtils.valueOf(DataSourceType.class, datasource.getType());
            dataSourceChooseVo.setType(type.getName());
            if (DataSourceType.ES_TYPE.equals(type)) {
                Result result = dataTableService.listDataSourceDataTables(datasource.getId());
                if (result.getCode() == ResultCode.OK.value()) {
                    dataSourceChooseVo.setTables((List<MetadataTableInfoOptions>) result.getResult());
                }
            }
            resultList.add(dataSourceChooseVo);
        }
        return ResultUtils.buildResult(ResultCode.OK, resultList);
    }
}
