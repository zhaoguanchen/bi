package com.yiche.bigdata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.constants.ConnectorType;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.constants.RowPermissionFilterType;
import com.yiche.bigdata.dao.FilterComponentDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.generated.FilterComponent;
import com.yiche.bigdata.entity.pojo.NodeMappingInfo;
import com.yiche.bigdata.entity.pojo.RowPermissionItem;
import com.yiche.bigdata.entity.pojo.RowPermissionOptionItem;
import com.yiche.bigdata.entity.vo.RowPermissionOptionVo;
import com.yiche.bigdata.entity.vo.TableColumnInfoVo;
import com.yiche.bigdata.service.RowPermissionService;
import com.yiche.bigdata.service.UserInfoService;
import com.yiche.bigdata.service.core.UserComponentService;
import com.yiche.bigdata.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.yiche.bigdata.utils.DataSetColmunUtils.getTypeKey;

@Service
public class RowPermissionServiceImpl implements RowPermissionService {

    @Autowired
    private FilterComponentDao filterComponentDao;

    @Autowired
    private UserComponentService userComponentService;

    @Autowired
    private DatasetServiceImpl dataSetService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public List<FilterComponent> findRowPermission(String roleId, String resId) {
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(resId)) {
            return null;
        }
        return filterComponentDao.findByRoleAndResId(roleId, resId);
    }

    @Override
    public Result listColumns(String resId) {
        Result dataSetResult = dataSetService.getDatasetByResId(resId);
        if (dataSetResult.getCode() != ResultCode.OK.value()) {
            return dataSetResult;
        }

        Dataset dataset = (Dataset) dataSetResult.getResult();

        Map<String, NodeMappingInfo> allColumns = userComponentService.getTableColumns(dataset);
        if (allColumns == null) {
            return ResultUtils.buildResult(ResultCode.DATA_SET_DATA_ERROR);
        }
        JSONObject dataSetJson = JSONObject.parseObject(dataset.getDataJson());
        JSONArray dataSetDimensions = dataSetJson.getJSONObject("schema").getJSONArray("dimension");
        List<TableColumnInfoVo> resultList = new ArrayList<>();

        for (int i = 0; i < dataSetDimensions.size(); i++) {
            JSONObject dataSetDimension = (JSONObject) dataSetDimensions.get(i);
            String column = dataSetDimension.getString("column");
            if (allColumns.get(column) != null) {
                NodeMappingInfo columnDetail = allColumns.get(column);
                TableColumnInfoVo tableColumnInfoVo = new TableColumnInfoVo();
                tableColumnInfoVo.setColumn(columnDetail.getKey());
                tableColumnInfoVo.setName(columnDetail.getName());
                tableColumnInfoVo.setType(DataSetColmunUtils.getTypeKey(columnDetail.getEs().getType()));
                tableColumnInfoVo.setFormat(columnDetail.getEs().getFormat());
                getGetPermissionOption(columnDetail, tableColumnInfoVo);
                resultList.add(tableColumnInfoVo);
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, resultList);
    }

    private void getGetPermissionOption(NodeMappingInfo nodeMappingInfo, TableColumnInfoVo tableColumnInfoVo) {
        String colName = nodeMappingInfo.getName();
        String colType = nodeMappingInfo.getEs().getType();
        String colTypeString = DataSetColmunUtils.getDisplayName(colType, colName);
        String dataType = DataSetColmunUtils.getTypeKey(colType);
        List<RowPermissionOptionVo> permissionOptionList = new ArrayList<>();
        List<RowPermissionFilterType> types = RowPermissionFilterType.getVauleTypeKeys(dataType);
        for (RowPermissionFilterType type : types) {
            permissionOptionList.add(buildRowPermissionOptionVo(type));
        }
        tableColumnInfoVo.setDisplay(colTypeString);
        tableColumnInfoVo.setOptions(permissionOptionList);
    }

    private RowPermissionOptionVo buildRowPermissionOptionVo(RowPermissionFilterType type) {
        return buildRowPermissionOptionVo(type, null, false);
    }

    private RowPermissionOptionVo buildRowPermissionOptionVo(RowPermissionFilterType type, List<String> value, boolean checked) {
        RowPermissionOptionVo rowPermissionOptionVo = new RowPermissionOptionVo();
        rowPermissionOptionVo.setKey(type.value());
        rowPermissionOptionVo.setName(type.getName());
        rowPermissionOptionVo.setChecked(checked);
        rowPermissionOptionVo.setValue(value);
        return rowPermissionOptionVo;
    }

    @Override
    public Result saveRowPermission(RowPermissionItem rowPermissionItem) {
        if (rowPermissionItem == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        String roleId = rowPermissionItem.getRoleId();
        String resId = rowPermissionItem.getResId();
        Result dataSetResult = dataSetService.getDatasetByResId(resId);
        if (dataSetResult.getCode() != ResultCode.OK.value()) {
            return dataSetResult;
        }
        Dataset dataset = (Dataset) dataSetResult.getResult();
        if (dataSetResult.getCode() != ResultCode.OK.value()) {
            return dataSetResult;
        }
        Map<String, NodeMappingInfo> allColumns = userComponentService.getTableColumns(dataset);
        if (allColumns == null) {
            return ResultUtils.buildResult(ResultCode.DATA_SET_DATA_ERROR);
        }
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(resId)) {

        }
        List<RowPermissionOptionItem> permissions = rowPermissionItem.getPermissions();
        if (permissions.size() > 5) {
            return ResultUtils.buildResult(ResultCode.DATA_SET_ROW_PERMISSION_OVER_LIMIT);
        }
        filterComponentDao.deleteByRoleAndResId(roleId, resId);
        if (permissions != null && permissions.size() != 0) {
            for (RowPermissionOptionItem item : permissions) {
                NodeMappingInfo columnDetail = allColumns.get(item.getColumn());
                if (columnDetail != null) {
                    if (StringUtils.isEmpty(item.getColumn())
                            || StringUtils.isEmpty(item.getType())
                            || CollectionUtils.isEmpty(item.getValue())) {
                        continue;
                    }
                    FilterComponent rowPermission = new FilterComponent();
                    rowPermission.setResId(resId);
                    rowPermission.setRoleId(roleId);
                    rowPermission.setResType(ResourceType.DATA_SET.value());
                    rowPermission.setColKey(columnDetail.getKey());
                    rowPermission.setColName(columnDetail.getName());
                    rowPermission.setConnectorType(ConnectorType.AND.value());
                    rowPermission.setColType(columnDetail.getEs().getType());
                    rowPermission.setFilterType(item.getKey());
                    rowPermission.setFilterValue(CommonUtils.listToString(item.getValue(), null));
                    String user = userInfoService.getUserNameByToken(TokenUtils.getToken());
                    rowPermission.setCreater(user);
                    rowPermission.setLastModifier(user);
                    filterComponentDao.save(rowPermission);
                }

            }
        }
        return ResultUtils.buildResult(ResultCode.OK);
    }

    @Override
    public Result listDataSetRowPermission(String roleId, String resId) {
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        List<FilterComponent> rowPermissionList = filterComponentDao.findByRoleAndResId(roleId, resId);
        if (CollectionUtils.isEmpty(rowPermissionList)) {
            return ResultUtils.buildResult(ResultCode.OK, new ArrayList<>());
        }
        List<RowPermissionOptionItem> resultVoList = new ArrayList<>();
        for (FilterComponent rowPermission : rowPermissionList) {
            if (rowPermission != null) {
                RowPermissionOptionItem rowPermissionVo = new RowPermissionOptionItem();
                rowPermissionVo.setColumn(rowPermission.getColKey());
                rowPermissionVo.setKey(rowPermission.getFilterType());
                rowPermissionVo.setColumnName(DataSetColmunUtils.getDisplayName(rowPermission.getColType(), rowPermission.getFilterType()));
                RowPermissionFilterType filterType = EnumUtils.valueOf(RowPermissionFilterType.class, rowPermission.getFilterType());
                if (filterType == null) {
                    continue;
                }
                rowPermissionVo.setOptionName(filterType.getName());
                rowPermissionVo.setValue(CommonUtils.stringToList(rowPermission.getFilterValue()));
                rowPermissionVo.setType(getTypeKey(rowPermission.getColType()));
                resultVoList.add(rowPermissionVo);
            }
        }
        return ResultUtils.buildResult(ResultCode.OK, resultVoList);
    }
}
