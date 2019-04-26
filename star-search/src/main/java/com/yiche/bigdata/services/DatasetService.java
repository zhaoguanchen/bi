package com.yiche.bigdata.services;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.dto.req.TimePrimaryKey;
import com.yiche.bigdata.pojo.DashboardDataset;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Created by yfyuan on 2016/10/11.
 */
@Repository
public class DatasetService {

    public TimePrimaryKey getDatasetTimePrimaryKey(DashboardDataset dashboardDataset) {
        TimePrimaryKey timePrimaryKey = null;
//        DashboardDataset dashboardDataset = datasetDao.getDataset(datasetId);

        if (dashboardDataset != null && StringUtils.isNotEmpty(dashboardDataset.getData())) {
            final String jsonDataString = dashboardDataset.getData();

            if (StringUtils.isNotEmpty(jsonDataString)) {
                JSONObject jsonDataObj = JSONObject.parseObject(jsonDataString);
                final String timePrimaryKeyString = jsonDataObj.getString("timePrimaryKey");
                if (StringUtils.isNotEmpty(timePrimaryKeyString)) {
                    try {
                        timePrimaryKey = JSONObject.parseObject(timePrimaryKeyString, TimePrimaryKey.class);
                    } catch (JSONException e) {
                        return timePrimaryKey;
                    }
                }
            }
        }
        return timePrimaryKey;
    }

    /*@Autowired
    private DatasetDao datasetDao;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private RoleDao roleDao;

    public ServiceStatus save(String userId, String json) {
        JSONObject pageOriginalJsonObject = JSONObject.parseObject(json);
        JSONObject jsonObject = pageOriginalJsonObject;
        if (pageOriginalJsonObject.containsKey("data")
                && pageOriginalJsonObject.get("data") != null) {
            JSONObject dateyObject = (JSONObject) jsonObject.get("data");
            JSONObject queryObject = (JSONObject) dateyObject.get("query");
            if (queryObject != null && !queryObject.containsKey("index")) {
                jsonObject = this.replaceIndexAndTypeFromMetadataByTable(pageOriginalJsonObject);
            }
        }

        DashboardDataset dataset = new DashboardDataset();
        dataset.setUserId(userId);
        dataset.setName(jsonObject.getString("name"));
        dataset.setData(jsonObject.getString("data"));
        dataset.setCategoryName(jsonObject.getString("categoryName"));
        if (StringUtils.isEmpty(dataset.getCategoryName())) {
            dataset.setCategoryName("默认分类");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("dataset_name", dataset.getName());
        paramMap.put("user_id", dataset.getUserId());
        paramMap.put("category_name", dataset.getCategoryName());
        if (datasetDao.countExistDatasetName(paramMap) <= 0) {
            datasetDao.save(dataset);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "Duplicated name");
        }
    }

    public List<String> getDataSetColumnKeys(Long dataSetId) {
        DashboardDataset dataset = getDataSetById(dataSetId);
        String dataSetJSONData = dataset.getData();

        //may need to be added on null check, let's see how it goes
        return //colKeyList
                ((JSONArray) ((Map) ((Map) JSONObject.parse(dataSetJSONData))
                        .get("schema"))
                        .get("dimension"))
                        .stream()
                        .map(colJsonObj -> {
                            JSONObject col = (JSONObject) colJsonObj;
                            Object colKeyObj = col.get("column");
                            return colKeyObj == null ? "" : (String) colKeyObj;
                        }).filter(obj -> obj != null).collect(Collectors.toList());

    }



    public boolean hasTimePrimaryKey(Long datasetId) {
        boolean hasTimePrimaryKey = false;
        final List<String> cols = getDataSetColumnKeys(datasetId);
        if (CollectionUtils.isNotEmpty(cols)) {
            final TimePrimaryKey timePrimaryKey = getDatasetTimePrimaryKey(datasetId);
            final String timePrimaryKeyName = ObjectUtils.isEmpty(timePrimaryKey) ? "" : timePrimaryKey.getColumn();
            if (StringUtils.isNotEmpty(timePrimaryKeyName)) {
                hasTimePrimaryKey = cols.contains(timePrimaryKeyName);
            }
        }
        return hasTimePrimaryKey;
    }

    public DashboardDataset getDataSetById(Long dataSetId) {
        return datasetDao.getDataset(dataSetId);
    }

    public ServiceStatus update(String userId, String json) {
        JSONObject pageOriginalJsonObject = JSONObject.parseObject(json);
        JSONObject jsonObject = pageOriginalJsonObject;
        if (pageOriginalJsonObject.containsKey("data")
                && pageOriginalJsonObject.get("data") != null) {
            JSONObject dateyObject = (JSONObject) jsonObject.get("data");
            JSONObject queryObject = (JSONObject) dateyObject.get("query");
            if (queryObject != null && !queryObject.containsKey("index")) {
                jsonObject = this.replaceIndexAndTypeFromMetadataByTable(pageOriginalJsonObject);
            }
        }
        DashboardDataset dataset = new DashboardDataset();
        dataset.setUserId(userId);
        dataset.setId(jsonObject.getLong("id"));
        dataset.setName(jsonObject.getString("name"));
        dataset.setCategoryName(jsonObject.getString("categoryName"));
        dataset.setData(jsonObject.getString("data"));
        if (StringUtils.isEmpty(dataset.getCategoryName())) {
            dataset.setCategoryName("默认分类");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("dataset_name", dataset.getName());
        paramMap.put("user_id", dataset.getUserId());
        paramMap.put("dataset_id", dataset.getId());
        paramMap.put("category_name", dataset.getCategoryName());
        if (datasetDao.countExistDatasetName(paramMap) <= 0) {
            datasetDao.update(dataset);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "Duplicated name");
        }
    }

    public ServiceStatus delete(String userId, Long id) {
        datasetDao.delete(id, userId);
        return new ServiceStatus(ServiceStatus.Status.Success, "success");
    }

    *//**
     * 从元数据获取index和type来替换 请求json中的query里面的table关键字
     *
     * @param jsonObjectSrc
     * @return
     *//*
    private JSONObject replaceIndexAndTypeFromMetadataByTable(JSONObject jsonObjectSrc) {
        JSONObject jsonObject = jsonObjectSrc;
        if (jsonObject != null && jsonObject.containsKey("data")) {
            JSONObject dateyObject = (JSONObject) jsonObject.get("data");
            JSONObject queryObject = (JSONObject) dateyObject.get("query");
            if (queryObject.containsKey("table")) {
                String tableName = queryObject.get("table").toString();
                TableDetail tableDetail = this.metadataService.getTableDetail(tableName);
                if (tableDetail != null) {
                    String index = tableDetail.getIndex();
                    queryObject.put("index", index + "*");
//                    queryObject.put("type", tableName);
//                queryObject.remove("table");
                    ((JSONObject) jsonObject.get("data")).put("query", queryObject);
                    return jsonObject;
                }
            }

        }

        return jsonObjectSrc;
    }

    public List<DashboardDataset> getDatasetListByUserIdNotAdmin(String userId) {

        List<DashboardDataset> datasetList = Lists.newArrayList();
        List<DashboardRoleRes> roleResListCategory = roleDao.getUserRoleResList(userId, "datasetcategory",
                "");
        if (roleResListCategory != null && !roleResListCategory.isEmpty()) {
            for (DashboardRoleRes dashboardRoleRes : roleResListCategory) {
                List<DashboardDataset> categoryDatasetList = datasetDao.getDatasetListByCategoryId(dashboardRoleRes
                        .getResId());
                datasetList.addAll(categoryDatasetList);
            }
        }
        List<DashboardRoleRes> roleResListEntityList = roleDao.getUserRoleResList(userId, "dataset", "");
        if (roleResListEntityList != null && !roleResListEntityList.isEmpty()) {
            for (DashboardRoleRes dashboardRoleRes : roleResListEntityList) {
                if (dashboardRoleRes.getResId() == null) {
                    break;
                }
                DashboardDataset tempDataset = datasetDao.getDataset(Long.parseLong(dashboardRoleRes.getResId()));
                datasetList.add(tempDataset);
            }
        }

        return datasetList;
    }*/
}
