package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Report;
import com.yiche.bigdata.entity.vo.DirectoryVO;
import com.yiche.bigdata.entity.vo.ReportVO;

public interface ReportService {

    /**
     * 根据主键查询一条报表数据
     *
     * @param resId
     * @return Result<T>.class
     */
    Result getReportByResId(String resId);

    /**
     * 新增报表目录，同时新增资源树
     *
     * @param directoryVO
     * @return Result<T>.class
     */
    Result addReportDirectory(DirectoryVO directoryVO);

    /**
     * 更新报表目录，同时更新资源树
     *
     * @param directoryVO
     * @return
     */
    Result updateReportDirectory(DirectoryVO directoryVO);

    /**
     * 删除报表目录，同时删除资源树
     *
     * @param resId
     * @return
     */
    Result deleteReportDirectory(String resId);

    /**
     * 新增报表，同时新增资源树
     *
     * @param reportVO
     * @return Result<T>.class
     */
    Result addReport(ReportVO reportVO);

    /**
     * 复制报表，同时复制资源树
     *
     * @param resId
     * @return Result<T>.class
     */
    Result copyReport(String resId);

    /**
     * 修改报表，同时修改资源树
     *
     * @param report
     * @return Result<T>.class
     */
    Result updateReport(Report report);

    /**
     * 移动报表，同时修改资源树
     *
     * @param resId
     * @param targetDirectoryId
     * @return
     */
    Result moveReport(String resId, String targetDirectoryId);

    /**
     * 删除报表，同时删除资源树
     *
     * @param resId
     * @return Result<T>.class
     */
    Result deleteReport(String resId);

}
