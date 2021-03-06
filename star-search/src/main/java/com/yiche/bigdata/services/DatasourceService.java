//package com.yiche.bigdata.services;
//
//import com.alibaba.fastjson.JSONObject;
//import com.yiche.bigdata.dao.DatasourceDao;
//import com.yiche.bigdata.dataprovider.DataProviderManager;
//import com.yiche.bigdata.dataprovider.annotation.DatasourceParameter;
//import com.yiche.bigdata.dto.ViewDashboardDatasource;
//import com.yiche.bigdata.pojo.DashboardDatasource;
//import com.yiche.bigdata.services.role.RolePermission;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//
///**
// * Created by yfyuan on 2016/8/19.
// */
//@Repository
//public class DatasourceService {
//
//    @Autowired
//    private DatasourceDao datasourceDao;
//
//    public List<ViewDashboardDatasource> getViewDatasourceList(Supplier<List<DashboardDatasource>> daoQuery) {
//        List<DashboardDatasource> list = daoQuery.get();
//        List<ViewDashboardDatasource> vlist = list.stream().map(e -> (ViewDashboardDatasource) ViewDashboardDatasource.TO.apply(e)).collect(Collectors.toList());
//        vlist.forEach(e -> {
//            try {
//                List<String> fields = DataProviderManager.getProviderFieldByType(e.getType(), DatasourceParameter.Type.Password);
//                fields.forEach(f -> {
//                    if (e.getConfig().containsKey(f)) {
//                        e.getConfig().put(f, "");
//                    }
//                });
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//        return vlist;
//    }
//
//    public ServiceStatus save(String userId, String json) {
//        JSONObject jsonObject = JSONObject.parseObject(json);
//        DashboardDatasource datasource = new DashboardDatasource();
//        datasource.setUserId(userId);
//        datasource.setName(jsonObject.getString("name"));
//        datasource.setType(jsonObject.getString("type"));
//        datasource.setConfig(jsonObject.getString("config"));
//
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("user_id", datasource.getUserId());
//        paramMap.put("source_name", datasource.getName());
//        if (datasourceDao.countExistDatasourceName(paramMap) <= 0) {
//            datasourceDao.save(datasource);
//            return new ServiceStatus(ServiceStatus.Status.Success, "success");
//        } else {
//            return new ServiceStatus(ServiceStatus.Status.Fail, "名称重复");
//        }
//    }
//
//    public ServiceStatus update(String userId, String json) {
//        JSONObject jsonObject = JSONObject.parseObject(json);
//        DashboardDatasource datasource = new DashboardDatasource();
//        datasource.setUserId(userId);
//        datasource.setName(jsonObject.getString("name"));
//        datasource.setType(jsonObject.getString("type"));
//        datasource.setConfig(jsonObject.getString("config"));
//        datasource.setId(jsonObject.getLong("id"));
//
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("datasource_id", datasource.getId());
//        paramMap.put("user_id", datasource.getUserId());
//        paramMap.put("source_name", datasource.getName());
//        if (datasourceDao.countExistDatasourceName(paramMap) <= 0) {
//            datasourceDao.update(datasource);
//            return new ServiceStatus(ServiceStatus.Status.Success, "success");
//        } else {
//            return new ServiceStatus(ServiceStatus.Status.Fail, "名称重复");
//        }
//    }
//
//    public ServiceStatus delete(String userId, Long id) {
//        datasourceDao.delete(id, userId);
//        return new ServiceStatus(ServiceStatus.Status.Success, "success");
//    }
//
//    public ServiceStatus checkDatasource(String userId, Long id) {
//        DashboardDatasource datasource = datasourceDao.getDatasource(id);
//        if (datasourceDao.checkDatasourceRole(userId, id, RolePermission.PATTERN_READ) == 1) {
//            return new ServiceStatus(ServiceStatus.Status.Success, "success");
//        } else {
//            return new ServiceStatus(ServiceStatus.Status.Fail, datasource.getName());
//        }
//    }
//}
