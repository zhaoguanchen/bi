package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.vo.DatasourceVO;

import java.util.List;

public interface DatasourceService {

    /**
     * 获取所有数据
     * @return Result<List<Datasource>>.class
     */
    Result<List<Datasource>> getDatasourceList();

    /**
     * 根据主键查询一条数据源
     * @param id
     * @return Result<T>.class
     */
    Result getDatasourceByResId(String id);

    /**
     * 新增数据源
     * @param datasourceVO
     * @return Result<T>.class
     */
    Result addDatasource(DatasourceVO datasourceVO);

    /**
     * 修改数据源
     * @param datasource
     * @return Result<T>.class
     */
    Result updateDatasource(Datasource datasource);

    /**
     * 删除数据源
     * @param id
     * @return Result<T>.class
     */
    Result deleteDatasource(String id);

}
