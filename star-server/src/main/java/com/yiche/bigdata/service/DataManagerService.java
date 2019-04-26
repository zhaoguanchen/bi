package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.WidgetDataSearchItem;
import org.springframework.http.ResponseEntity;

public interface DataManagerService {

    /**
     * 根据数据源和查询语句获取列参数
     * @param datasourceId
     * @param query
     * @param reload
     * @return Result.class
     */
    Result getColumns(String datasourceId,String query,Boolean reload);

    /**
     * 根据主键下载数据
     * @param resId
     * @return ResponseEntity<byte[]>.class
     */
    ResponseEntity<byte[]> downloadExcel(String resId);

    /**
     * 根据数据集获取图表数据
     * @param datasetId
     * @param timeFilterString
     * @param hasLinkRatio
     * @param hasYoYRatio
     * @param cfgString
     * @param reload
     * @return Result.class
     */
    Result getAggregateData(String datasetId,String timeFilterString,Boolean hasLinkRatio,Boolean hasYoYRatio,String cfgString,Boolean reload);

    /**
     * 查询图表数据
     * @param dataSearchItem
     * @return Result.class
     */
    Result getWidgetData(WidgetDataSearchItem dataSearchItem);


    /**
     * 查询数据集指定列的所有值
     * @param query
     * @param datasetId
     * @param colmunName
     * @param cfg
     * @param reload
     * @return
     */
    Result getDimensionValues(String query, String datasetId, String colmunName, String cfg, Boolean reload);


    /**
     * 测试数据源连接
     * @param type
     * @param config
     * @param query
     * @return Result<T>.class
     */
    Result testConnect(String type, String config, String query);
}
