package com.yiche.bigdata.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.constants.ConnectorType;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.constants.RoleType;
import com.yiche.bigdata.dao.WidgetDao;
import com.yiche.bigdata.entity.dto.FilterPartExpression;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.generated.FilterComponent;
import com.yiche.bigdata.entity.generated.Permission;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.pojo.NodeMappingInfo;
import com.yiche.bigdata.entity.pojo.TableDetail;
import com.yiche.bigdata.entity.vo.ComponentPermissionVo;
import com.yiche.bigdata.entity.vo.ResourceSelectorVo;
import com.yiche.bigdata.service.DataTableService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserComponentService {


    @Autowired
    private DataTableService dataTableService;

    @Autowired
    private UserContextContainer userContextContainer;

    @Autowired
    private ResourceCenter resourceCenter;

    @Autowired
    private WidgetDao widgetDao;

    private static final String DOWNLOAD_PERMISSION_KEY = "download";

    private static final String DRILL_DOWN_PERMISSION_KEY = "drilldown";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserComponentService.class);

    public Map<String, String> getBusinessLineLevelPermission(String businessLine, ResourceType resourceType){
        UserContext userContext = getUserContext();
        RoleType roleType = userContext.getMaxRole(businessLine);
        Map<String, String> permissions = new HashMap<>();
        String permissionKeys = userContext.getResourcePermissions().get(businessLine);
        if(roleType.equals(RoleType.SUPER_ADMIN_TYPE) || roleType.equals(RoleType.BUSINESS_ADMIN_TYPE)){
            String[] BusinessLineLevelPermissions = ResourceType.getBusinessLineLevelPermission(resourceType.value());
            for (String businessLineLevelPermission : BusinessLineLevelPermissions){
                Permission permission = resourceCenter.getPermissions().get(businessLineLevelPermission);
                if(permission != null){
                    permissions.put(permission.getKey(), permission.getName());
                }
            }
        }else if(StringUtils.isNotEmpty(permissionKeys)){
            String[] BusinessLineLevelPermissions = ResourceType.getBusinessLineLevelPermission(resourceType.value());
            for (String businessLineLevelPermission : BusinessLineLevelPermissions){
                if(permissionKeys.contains(businessLineLevelPermission)){
                    Permission permission = resourceCenter.getPermissions().get(businessLineLevelPermission);
                    if(permission != null){
                        permissions.put(permission.getKey(), permission.getName());
                    }
                }
            }
        }
        return permissions;
    }


    public Map<String, String> getComponentOptPermission(BaseNode node, String businessLine){
        UserContext userContext = getUserContext();
        RoleType roleType = userContext.getMaxRole(businessLine);
        Map<String, String> permissions = new HashMap<>();
        if(node != null){
            String permissionKeys = userContext.getResourcePermissions().get(node.getId());
            if(roleType.equals(RoleType.SUPER_ADMIN_TYPE) || roleType.equals(RoleType.BUSINESS_ADMIN_TYPE)
                    || userContext.getUserId().equals(node.getCreater())){
                List<Permission> adminPermissions = resourceCenter.getResOpt().get(String.valueOf(node.getType().value()));
                for (Permission permission : adminPermissions) {
                    permissions.put(permission.getKey(), permission.getName());
                }
            }else {
                permissions = getPermissionObjMap(permissionKeys);
            }
        }
        return permissions;
    }

    public Map<String, String> getPermissionObjMap(String permissionKeys) {
        Map<String, String> permissions = new HashMap<>();
        if(StringUtils.isNotEmpty(permissionKeys)){
            for (String permissionKey : permissionKeys.split(",")) {
                Permission permission = resourceCenter.getPermissions().get(permissionKey);
                if(permission != null){
                    permissions.put(permission.getKey(), permission.getName());
                }
            }
        }
        return permissions;
    }

    public List<ComponentPermissionVo> getWidgetsPermission(String layoutJson, String businessLine) {
        UserContext userContext = getUserContext();
        RoleType roleType = userContext.getMaxRole(businessLine);
        List<Widget> widgetList = getWidgetsByLayoutJson(layoutJson);
        List<ComponentPermissionVo> componentPermissionVoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(widgetList)){
            Map<String, Map<String, String>>  dataSetPermissionsCache = new HashMap<>();
            for (Widget widget : widgetList) {
                String dataSetId = widget.getDatasetId();
                ComponentPermissionVo widgetPermissionVo = new ComponentPermissionVo();
                if(StringUtils.isNotEmpty(dataSetId)){
                    Map<String, String> dataSetPermissions = getWidgetPermissionFromDataSet(userContext, roleType, dataSetPermissionsCache, dataSetId);
                    widgetPermissionVo.setPermissions(dataSetPermissions);
                }
                BeanUtils.copyProperties(widget, widgetPermissionVo);
                componentPermissionVoList.add(widgetPermissionVo);
            }
        }
        return componentPermissionVoList;
    }

    public Map<String, String> getWidgetPermissionFromDataSet(UserContext userContext, RoleType roleType,
                                   Map<String, Map<String, String>> dataSetPermissionsCache, String dataSetId) {
        Map<String, String> dataSetPermissions = dataSetPermissionsCache.get(dataSetId);
        if(dataSetPermissions == null){
            dataSetPermissions = new HashMap<>();
            String dataSetPermissionKeys =  userContext.getResourcePermissions().get(dataSetId);
            if(roleType.equals(RoleType.SUPER_ADMIN_TYPE) || roleType.equals(RoleType.BUSINESS_ADMIN_TYPE)){
                List<Permission> adminPermissions = resourceCenter.getResOpt().get(Integer.toString(ResourceType.DATA_SET.value()));
                for (Permission permission : adminPermissions) {
                    if(DOWNLOAD_PERMISSION_KEY.equals(permission.getKey())
                            || DRILL_DOWN_PERMISSION_KEY.equals(permission.getKey())){
                        dataSetPermissions.put(permission.getKey(), permission.getName());

                    }
                }
            }else if(StringUtils.isNotEmpty(dataSetPermissionKeys)){
                for (String permissionKey : dataSetPermissionKeys.split(",")) {
                    if(DOWNLOAD_PERMISSION_KEY.equals(permissionKey)
                            || DRILL_DOWN_PERMISSION_KEY.equals(permissionKey)){
                        Permission permission = resourceCenter.getPermissions().get(permissionKey);
                        if(permission != null){
                            dataSetPermissions.put(permission.getKey(), permission.getName());
                        }
                    }
                }
            }
            dataSetPermissionsCache.put(dataSetId, dataSetPermissions);
        }
        return dataSetPermissions;
    }


    private List<Widget> getWidgetsByLayoutJson(String layoutJson) {
        if(StringUtils.isEmpty(layoutJson)){
            return null;
        }
        try{
            JSONObject components = JSONObject.parseObject(layoutJson);
            List<String> widgetIdList = new ArrayList<>();
            JSONArray rows = components.getJSONArray("rows");
            if(rows != null){
                for (int i = 0; i < rows.size(); i++) {
                    JSONArray widgetJsonList = rows.getJSONObject(i).getJSONArray("widgets");
                    if(widgetJsonList != null){
                        for(int j = 0; j < widgetJsonList.size(); j++){
                            JSONObject widget = widgetJsonList.getJSONObject(j);
                            String widgetId = widget.getString("widgetId");
                            if(StringUtils.isNotEmpty(widgetId)){
                                widgetIdList.add(widgetId);
                            }
                        }
                    }
                }
            }
            if(! CollectionUtils.isEmpty(widgetIdList)){
                return widgetDao.findWidgets(widgetIdList);
            }
        }catch (JSONException exception){
            LOGGER.info("layout json [{} - {}] data json parse failure [{}] ... ", layoutJson);
        }
        return null;
    }

    public List<FilterPartExpression> getUserDateSetRowPermission(String resId){
        List<FilterComponent> rowPermissionList = getUserContext().getDataSetRowPermission(resId);
        if(CollectionUtils.isEmpty(rowPermissionList)){
            return null;
        }
        List<FilterPartExpression> filterPartExpressions = new ArrayList<>();
        for (FilterComponent rowPermission : rowPermissionList) {
            FilterPartExpression filterPartExpression = new FilterPartExpression();
            filterPartExpression.setConnector(ConnectorType.OR.getName());
            filterPartExpression.setColumn(rowPermission.getColKey());
            filterPartExpression.setType(rowPermission.getColType());
            filterPartExpression.setOperator(rowPermission.getFilterType());
            filterPartExpression.setValues(CommonUtils.stringToList(rowPermission.getFilterValue()));
            filterPartExpressions.add(filterPartExpression);
        }
        return filterPartExpressions;
    }

    private UserContext getUserContext() {
        String token = TokenUtils.getToken();
        return userContextContainer.getUserContext(token);
    }

    public List<ResourceSelectorVo> listUserResourceSelectorVo(String pid, Integer resourceType, String filter){
        UserContext userContext = getUserContext();
        BaseNode parentNode = userContext.getResTree().getNodeMapping().get(pid);
        if(parentNode == null || parentNode.getChildrenNodes() == null
                || parentNode.getChildrenNodes().size() == 0){
            return null;
        }
        List<ResourceSelectorVo> resultList = new ArrayList<>();
        for (BaseNode child : parentNode.getChildrenNodes()) {
            ResourceSelectorVo resourceSelectVo = new ResourceSelectorVo();
            resourceSelectVo.setName(child.getName());
            resourceSelectVo.setResId(child.getId());
            if(child.getType().value() == resourceType){
                resourceSelectVo.setType("resource");
                if(ResourceType.WIDGET.value() == resourceType){
                    //Widget的关联资源为dataSetID
                    Widget widget = resourceCenter.getWidgets().get(child.getId());
                    if(widget == null){
                        try {
                            widget = widgetDao.getWidgetByResId(child.getId());
                            JSONObject dataJson = JSONObject.parseObject(widget.getDataJson());
                            String dataSetId = dataJson.getString("datasetId");
                            if(StringUtils.isNotEmpty(dataSetId)){
                                widget.setDatasetId(dataSetId);
                            }
                        }catch (Exception exception){
                            LOGGER.info("widget [{}] data load failure [{}] ... ", child.getId());
                        }
                    }
                    String dataSetId = resourceCenter.getWidgets().get(child.getId()).getDatasetId();
                    resourceSelectVo.setRelyResId(dataSetId);
                    if(StringUtils.isEmpty(filter) || filter.equalsIgnoreCase(filter)){
                        resultList.add(resourceSelectVo);
                    }else{
                        resultList.add(resourceSelectVo);
                    }
                }else {
                    resultList.add(resourceSelectVo);
                }
            }else if(ResourceType.getDirectoryType(resourceType) == child.getType().value()){
                resourceSelectVo.setType("directory");
                List<ResourceSelectorVo> childrenVo = listUserResourceSelectorVo(child.getId(), resourceType, filter);
                if(! CollectionUtils.isEmpty(childrenVo)){
                    resourceSelectVo.setChildren(listUserResourceSelectorVo(child.getId(), resourceType, filter));
                    resultList.add(resourceSelectVo);
                }
            }
        }
        return resultList;
    }



    public Map<String, NodeMappingInfo> getTableColumns(Dataset dataset) {
        Map<String, NodeMappingInfo> allColumns = new HashMap<>();

        TableDetail tableDetail = getDateSetDetail(dataset);

        if(tableDetail == null){
            return null;
        }

        List<NodeMappingInfo> dimenList = tableDetail.getDimenList();
        if(! CollectionUtils.isEmpty(dimenList)){
            for (NodeMappingInfo dimen : dimenList) {
                allColumns.put(dimen.getKey(), dimen);
            }
        }

        List<NodeMappingInfo> metricList = tableDetail.getMetricList();
        if(! CollectionUtils.isEmpty(metricList)){
            for (NodeMappingInfo metric : metricList) {
                allColumns.put(metric.getKey(), metric);
            }
        }
        return allColumns;
    }

    private TableDetail getDateSetDetail(Dataset dataset) {
        try{
            JSONObject components = JSONObject.parseObject(dataset.getDataJson());
            String table = components.getJSONObject("query").getString("table");
            Result tableDetailResult = dataTableService.getMetadataTableInfo(table);
            if(tableDetailResult.getCode() != ResultCode.OK.value()){
                return null;
            }
            return (TableDetail) tableDetailResult.getResult();
        } catch (JSONException e){
            LOGGER.error("date set [{}] data json parse error: [{}]", dataset.getResId(), dataset.getDataJson());
            LOGGER.error("parse json error", e);
            return null;
        }
    }

}
