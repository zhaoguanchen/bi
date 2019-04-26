package com.yiche.bigdata.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(value = "star-search")
public interface SearchServerFeignService {

    /**
     * 获取库表当前条件下所有的字段
     * @param dashboardDatasourceString
     * @param dashboardDatasetString
     * @param query
     * @param reload
     * @return
     */
    @RequestMapping(value = "/dashboard/getColumns")
    Object getColumns(@RequestParam(name = "dashboardDatasource") String dashboardDatasourceString,
                                         @RequestParam(name = "dashboardDataset") String dashboardDatasetString,
                                         @RequestParam(name = "query", required = false) String query,
                                         @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload);

    /**
     * 获取当前查询条件下的所有数据
     * @param dashboardDatasourceString
     * @param dashboardDatasetString
     * @param query
     * @param timeFilterString
     * @param hasLinkRatio
     * @param hasYoYRatio
     * @param cfgString
     * @param reload
     * @param dashBoardDatasetWithRowEnumsString
     * @param filterPartExpressions
     * @return
     */
    @RequestMapping(value = "/dashboard/getAggregateData")
    Object getAggregateData(@RequestParam(name = "dashboardDatasource") String dashboardDatasourceString,
                                   @RequestParam(name = "dashboardDataset", required = false) String dashboardDatasetString,
                                   @RequestParam(name = "query", required = false) String query,
                                   @RequestParam(name = "timeFilter", required = false) String timeFilterString,
                                   @RequestParam(name = "hasLinkRatio", required = false) Boolean hasLinkRatio,
                                   @RequestParam(name = "hasYoYRatio", required = false) Boolean hasYoYRatio,
                                   @RequestParam(name = "cfg") String cfgString,
                                   @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload,
                                   @RequestParam(name = "dashBoardDatasetWithRowEnums", required = false) String dashBoardDatasetWithRowEnumsString,
                                   @RequestParam(name = "filterPartExpressions", required = false) String filterPartExpressions);


    /**
     * 查询数据集列的值
     * @param dashboardDatasourceString
     * @param dashboardDatasetString
     * @param query
     * @param colmunName
     * @param cfg
     * @param reload
     * @return
     */
    @RequestMapping(value = "/dashboard/getDimensionValues")
    Object getDimensionValues(@RequestParam(name = "dashboardDatasource", required = true) String dashboardDatasourceString,
                                             @RequestParam(name = "dashboardDataset", required = false) String dashboardDatasetString,
                                             @RequestParam(name = "query", required = false) String query,
                                             @RequestParam(name = "colmunName", required = true) String colmunName,
                                             @RequestParam(name = "cfg", required = false) String cfg,
                                             @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload);


    /**
     * 测试数据源连接
     * @param type
     * @param query
     * @param config
     * @return
     */
    @RequestMapping(value = "/dashboard/test")
    String testConnect(@RequestParam(name = "type")String type,
                       @RequestParam(name = "config")String config,
                       @RequestParam(name = "query")String query);


    /**
     * 自定义字段测试
     * @param dashboardDatasourceString
     * @param query
     * @return
     */
    @RequestMapping(value = "/dashboard/addColumnTest")
    String addColumnTest(@RequestParam(name = "dashboardDatasource", required = true) String dashboardDatasourceString,
                         @RequestParam(name = "query") String query,
                         @RequestParam(name = "cfg") String cfgString,
                         @RequestParam(name = "reload")  boolean reload);
}
