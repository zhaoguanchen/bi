package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dashboard;
import com.yiche.bigdata.entity.vo.DashboardVO;

public interface DashboardService {

    /**
     * 根据主键查询一条大盘数据
     * @param resId
     * @return Result<Dashboard>.class
     */
    Result<Dashboard> getDashboardByResId(String resId);

    /**
     * 新增大盘数据，同时添加到对应资源树
     * @param dashboardVO
     * @return Result.class
     */
     Result addDashboard(DashboardVO dashboardVO);

    /**
     * 修改大盘数据，同时修改对应资源树
     * @param dashboard
     * @return Result.class
     */
    Result updateDashboard(Dashboard dashboard);

}
