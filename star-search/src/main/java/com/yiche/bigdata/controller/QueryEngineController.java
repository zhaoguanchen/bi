package com.yiche.bigdata.controller;

import com.yiche.bigdata.dataprovider.DataProvider;
import com.yiche.bigdata.dataprovider.DataProviderManager;
import com.yiche.bigdata.dataprovider.result.AggregateResult;
import com.yiche.bigdata.dto.PairData;
import com.yiche.bigdata.pojo.QueryRequestBody;
import com.yiche.bigdata.services.DataProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class QueryEngineController {
    private static final Logger logger = LoggerFactory.getLogger(QueryEngineController.class);

    @Autowired
    private DataProviderService dataProviderService;

  /*  @RequestMapping(value = "/star/queryengine/queryAggData", method = RequestMethod.POST)
    public AggregateResult queryAggData(@RequestBody QueryRequestBody request) {
        try {
            DataProvider dataProvider = dataProviderService.getDataProvider();
            AggregateResult aggData = dataProvider.getAggData(request.getAggConfig(), request.getReload());
            return aggData;
        } catch (Exception e) {
            logger.error("异常信息", e);
        }
        return null;
    }
*/
    /*@RequestMapping(value = "/star/queryengine/queryAggData", method = RequestMethod.POST)
    public AggregateResult queryAggData(@RequestBody QueryRequestBody request) {
        try {
            DataProvider dataProvider = dataProviderService.getDataProvider(request.getDatasourceId(),request.getQuery(), request.getDatasetDto());
            AggregateResult aggData = dataProvider.getAggData(request.getAggConfig(),request.getReload());
            return aggData;
        } catch (Exception e) {
            logger.error("异常信息", e);
        }
        return null;
    }

    @RequestMapping(value = "/star/queryengine/queryColumn", method = RequestMethod.POST)
    public String[] queryColumn(@RequestBody QueryRequestBody request) {
        try {
            DataProvider dataProvider = dataProviderService.getDataProvider(request.getDatasourceId(),request.getQuery(), request.getDatasetDto());
            String[] columnArray = dataProvider.getColumn(request.getReload());
            return columnArray;
        } catch (Exception e) {
            logger.error("异常信息", e);
        }
        return null;
    }

    @RequestMapping(value = "/star/queryengine/queryDimVals", method = RequestMethod.POST)
    public List<PairData> queryDimVals(@RequestBody QueryRequestBody request) {
        try {
            DataProvider dataProvider = dataProviderService.getDataProvider(request.getDatasourceId(),request.getQuery(), request.getDatasetDto());
            List<PairData> result = dataProvider.getDimVals(request.getColumnName(),request.getAggConfig(),request.getReload());
            return result;
        } catch (Exception e) {
            logger.error("异常信息", e);
        }
        return null;
    }

    @RequestMapping(value = "/star/queryengine/queryType", method = RequestMethod.POST)
    public Map<String, String> queryType(@RequestBody QueryRequestBody request) {
        try {
            DataProvider dataProvider = dataProviderService.getDataProvider(request.getDatasourceId(),request.getQuery(), request.getDatasetDto());
            Map<String, String> result = dataProvider.getType();
            return result;
        } catch (Exception e) {
            logger.error("异常信息", e);
        }
        return null;
    }

    @RequestMapping(value = "/star/queryengine/queryViewAggDataQuery", method = RequestMethod.POST)
    public String queryViewAggDataQuery(@RequestBody QueryRequestBody request) {
        try {
            DataProvider dataProvider = dataProviderService.getDataProvider(request.getDatasourceId(),request.getQuery(), request.getDatasetDto());
            String result = dataProvider.getViewAggDataQuery(request.getAggConfig());
            return result;
        } catch (Exception e) {
            logger.error("异常信息", e);
        }
        return null;
    }

    @RequestMapping(value = "/star/queryengine/queryData", method = RequestMethod.POST)
    public String[][] queryData(@RequestBody QueryRequestBody request) {
        try {
            DataProvider dataProvider = DataProviderManager.getDataProvider(request.getType(),request.getDataSource(),request.getQuery());
            String[][] data = dataProvider.getData();
            return data;
        } catch (Exception e) {
            logger.error("异常信息", e);
        }
        return null;
    }
*/
    /*@RequestMapping(value = "/star/queryengine/test", method = RequestMethod.GET)
    public String test() {
        Datasource datasource = datasourceDao.getDatasource("108");
        return datasource.getConfig();
    }*/
}
