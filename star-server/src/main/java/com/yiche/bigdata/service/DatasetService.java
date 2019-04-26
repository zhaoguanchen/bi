package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.DatasetVO;
import com.yiche.bigdata.entity.vo.ExpressionValidateVO;

public interface DatasetService {

    /**
     * 根据主键查询一条数据集
     *
     * @param resId
     * @return Result<Dataset>.class
     */
    Result<Dataset> getDatasetByResId(String resId);

    /**
     * 新增数据集目录，同时新增资源树
     *
     * @param directoryVO
     * @return Result.class
     */
    Result addDataSetDirectory(DirectoryVO directoryVO);

    /**
     * 更新数据集目录
     *
     * @param directoryVO
     * @return
     */
    Result updateDataSetDirectory(DirectoryVO directoryVO);

    /**
     * 新增数据集，同时新增资源树
     *
     * @param datasetVO
     * @return Result.class
     */
    Result addDataset(DatasetVO datasetVO);

//    /**
//     * 复制数据集，同时新增资源树
//     *
//     * @param resId
//     * @return Result.class
//     */
//    Result copyDataset(String resId);

    /**
     * 修改数据集，同时修改资源树
     *
     * @param dataset
     * @return Result.class
     */
    Result updateDataset(Dataset dataset);

    /**
     * 删除数据集，同时删除资源树
     *
     * @param resId
     * @return Result.class
     */
    Result deleteDataset(String resId);


}
