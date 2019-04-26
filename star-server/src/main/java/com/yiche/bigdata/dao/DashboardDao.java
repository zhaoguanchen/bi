package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.Dashboard;
import com.yiche.bigdata.mapper.generated.DashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DashboardDao {

    @Autowired
    DashboardMapper dashboardMapper;

    public Dashboard getDashboardByResId(String resId) {
        return dashboardMapper.selectByPrimaryKey(resId);
    }

    public boolean addDashboard(Dashboard dashboard) {
        int result = dashboardMapper.insert(dashboard);
        return result > 0;
    }

    public boolean updateDashboard(Dashboard dashboard) {
        int result = dashboardMapper.updateByPrimaryKeyWithBLOBs(dashboard);
        return result > 0;
    }

}
