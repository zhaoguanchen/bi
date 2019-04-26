package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.vo.DataTableSelectVo;
import com.yiche.bigdata.entity.vo.ExpressionValidateVO;

import java.util.List;
import java.util.Map;

public interface DataSetPageService {

    /**
     * 查询数据集添加等权限
     *
     * @param businessLine
     * @return Result<T>.class
     */
    Result getDataSetCommonPermission(String businessLine);

    /**
     * 查询当前登录用户数据集列表
     *
     * @param businessLineId
     * @return
     */
    Result getDataSetList(String businessLineId);

    /**
     * 分页查询用户报表列表
     *
     * @param queryItem
     * @return Result<T>.class
     */
    Result getPagedDataSet(PagedQueryItem<Map> queryItem);

    /**
     * 查询当前登录用户可用数据源
     *
     * @param businessLine
     * @return Result<T>.class
     */
    Result listDataSource(String businessLine);


    /**
     * 查询当前登录用户数据源可用事实表
     *
     * @param businessLine
     * @return Result<T>.class
     */
    Result<Map<String, List<DataTableSelectVo>>> listDataSourceTables(String businessLine, String datasourceId);


    /**
     * 查询当前登录用户可用数据集下拉菜单
     *
     * @param pid
     * @param filter
     * @return Result<T>.class
     */
    Result getDataSetSelector(String pid, String filter);


    /**
     * 查询数据集下列信息
     *
     * @param dataSetIdList
     * @return Result<T>.class
     */
    Result getDataSetColumns(List<String> dataSetIdList);

    /**
     * 数据集、数据集文件夹搜索
     *
     * @param businessLineId
     * @param search
     * @return
     */
    Result getDataSetSearchList(String businessLineId, String search);


    /**
     * 搜索当前登录用户数据源可用事实表
     *
     * @param businessLine
     * @param datasourceId
     * @param condition
     * @return
     */
    Result<Map<String, List<DataTableSelectVo>>> listDataSourceTable(String businessLine, String datasourceId, String condition);

    /**
     * 预览数据集
     *
     * @param
     * @return
     */
    Result viewDataSet(String dateSet, String type);

    /**
     * 测试自定义字段表达式
     *
     * @param column
     * @param dataSourceId
     * @return
     */
    Result addColumnTest(String column, String dataSourceId, String query, String type);

    /**
     * 验证表达式
     *
     * @param expression
     * @return
     */
    Result expressionValidator(ExpressionValidateVO expression);
}
