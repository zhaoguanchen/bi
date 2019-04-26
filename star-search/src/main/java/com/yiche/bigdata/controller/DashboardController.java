package com.yiche.bigdata.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.yiche.bigdata.cache.CacheManager;
import com.yiche.bigdata.cache.HeapCacheManager;
import com.yiche.bigdata.dao.DatasourceDao;
import com.yiche.bigdata.dataprovider.config.AggConfig;
import com.yiche.bigdata.dataprovider.result.AggregateResult;
import com.yiche.bigdata.dto.DataProviderResult;
import com.yiche.bigdata.dto.PairData;
import com.yiche.bigdata.dto.ViewAggConfig;
import com.yiche.bigdata.dto.ViewDashboardDatasource;
import com.yiche.bigdata.dto.req.CfgReq;
import com.yiche.bigdata.dto.req.TimeFilterReq;
import com.yiche.bigdata.pojo.DashBoardDatasetWithRowEnum;
import com.yiche.bigdata.pojo.DashboardDataset;
import com.yiche.bigdata.pojo.DashboardDatasource;
import com.yiche.bigdata.pojo.FilterPartExpression;
import com.yiche.bigdata.services.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/9.
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    private static final String EMPTY_DATABASE = "empty_database";

//    @Autowired
//    private DatasourceService datasourceService;

    @Autowired
    private DataProviderService dataProviderService;
    @Autowired
    private LogService logService;

    @Value("#{'${admin_user_id}'.split(',')}")
    private List<String> adminUserId;

    private static final CacheManager<Map<String, String>> typesCache = new HeapCacheManager<>();


    @RequestMapping(value = "/getAggregateData")
    public AggregateResult getAggregateData(@RequestParam(name = "dashboardDatasource", required = true) String dashboardDatasourceString,
                                            @RequestParam(name = "dashboardDataset", required = false) String dashboardDatasetString,
                                            @RequestParam(name = "query", required = false) String query,
                                            @RequestParam(name = "timeFilter", required = false) String timeFilterString,
                                            @RequestParam(name = "hasLinkRatio", required = false) Boolean hasLinkRatio,
                                            @RequestParam(name = "hasYoYRatio", required = false) Boolean hasYoYRatio,
                                            @RequestParam(name = "cfg") String cfgString,
                                            @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload,
                                            @RequestParam(name = "dashBoardDatasetWithRowEnums", required = false) String dashBoardDatasetWithRowEnumsString,
                                            @RequestParam(name = "filterPartExpressions", required = false) String filterPartExpressions
    ) throws ParseException {

        final DashboardDatasource dashboardDatasource = JSONObject.parseObject(dashboardDatasourceString, DashboardDatasource.class);
        final DashboardDataset dashboardDataset = JSONObject.parseObject(dashboardDatasetString, DashboardDataset.class);

        final TimeFilterReq timeFilterReq = JSONObject.parseObject(timeFilterString, TimeFilterReq.class);
        final CfgReq cfgReq = JSONObject.parseObject(cfgString, CfgReq.class);
        List<DashBoardDatasetWithRowEnum> dashBoardDatasetWithRowEnums = JSONObject.parseObject(dashBoardDatasetWithRowEnumsString, List.class);
        List<FilterPartExpression> filterPartExpressionList = JSONObject.parseArray(filterPartExpressions, FilterPartExpression.class);
        Map<String, String> strParams = null;
        if (StringUtils.isNotEmpty(query)) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }

        return dataProviderService.queryAggData(dashboardDatasource, strParams, dashboardDataset, timeFilterReq, cfgReq, hasLinkRatio, hasYoYRatio, reload, dashBoardDatasetWithRowEnums, filterPartExpressionList);
    }



    @RequestMapping(value = "/addColumnTest")
    public AggregateResult addColumnTest(@RequestParam(name = "dashboardDatasource", required = true) String dashboardDatasourceString,
                                            @RequestParam(name = "query", required = true) String query,
                                            @RequestParam(name = "cfg") String cfgString,
                                            @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload
    ) throws ParseException {

        Map<String,String> queryMap=new HashMap<>();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(query)) {
            JSONObject queryO = JSONObject.parseObject(query);
            queryMap = Maps.transformValues(queryO, Functions.toStringFunction());
        }

        final DashboardDatasource dashboardDatasource = JSONObject.parseObject(dashboardDatasourceString, DashboardDatasource.class);

        final CfgReq cfgReq = JSONObject.parseObject(cfgString, CfgReq.class);

        return dataProviderService.addColumnTest(dashboardDatasource, queryMap,cfgReq,reload);
    }

    @RequestMapping(value = "/getColumns")
    public DataProviderResult getColumns(@RequestParam(name = "dashboardDatasource", required = true) String dashboardDatasourceString,
                                         @RequestParam(name = "dashboardDataset", required = false) String dashboardDatasetString,
                                         @RequestParam(name = "query", required = false) String query,
                                         @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload) {
        final DashboardDatasource dashboardDatasource = JSONObject.parseObject(dashboardDatasourceString, DashboardDatasource.class);
        final DashboardDataset dashboardDataset = JSONObject.parseObject(dashboardDatasetString, DashboardDataset.class);

        Map<String, String> strParams = null;
        if (query != null) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        return dataProviderService.getColumns(dashboardDatasource, strParams, dashboardDataset, reload);
    }

    @RequestMapping(value = "/getDimensionValues")
    public String [] getDimensionValues(@RequestParam(name = "dashboardDatasource", required = true) String dashboardDatasourceString,
                                             @RequestParam(name = "dashboardDataset", required = false) String dashboardDatasetString,
                                             @RequestParam(name = "query", required = false) String query,
                                             @RequestParam(name = "colmunName", required = true) String colmunName,
                                             @RequestParam(name = "cfg", required = false) String cfg,
                                             @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload) {

        final DashboardDatasource dashboardDatasource = JSONObject.parseObject(dashboardDatasourceString, DashboardDatasource.class);
        final DashboardDataset dashboardDataset = JSONObject.parseObject(dashboardDatasetString, DashboardDataset.class);

        Map<String, String> strParams = null;
        if (query != null) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        AggConfig config = null;
        if (cfg != null) {
            config = ViewAggConfig.getAggConfig(JSONObject.parseObject(cfg, ViewAggConfig.class));
        }
        return dataProviderService.getDimensionValues(dashboardDatasource, strParams, dashboardDataset, colmunName, config, reload);
    }

    /**
     * 测试数据源连接
     * @param type
     * @param query
     * @param config
     * @return
     */
    @RequestMapping(value = "/test")
    public String testConnect(@RequestParam(name = "type")String type,
                       @RequestParam(name = "config")String config,
                       @RequestParam(name = "query")String query){
        ServiceStatus serviceStatus =  dataProviderService.test(type, config, query);
        if(serviceStatus.getStatus().equals(ServiceStatus.Status.Success.toString())){
            return "success";
        }else {
            return serviceStatus.getMsg();
        }
    }

//    @RequestMapping(value = "/getDatasourceList")
//    public List<ViewDashboardDatasource> getDatasourceList() {
//        String userid = authenticationService.getCurrentUser().getUserId();
//        return datasourceService.getViewDatasourceList(() -> datasourceDao.getDatasourceList(userid));
//    }

}
