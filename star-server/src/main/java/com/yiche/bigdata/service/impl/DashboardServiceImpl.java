package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.DashboardDao;
import com.yiche.bigdata.dao.ResTreeDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Dashboard;
import com.yiche.bigdata.entity.vo.DashboardVO;
import com.yiche.bigdata.service.DashboardService;
import com.yiche.bigdata.service.ResourceTreeService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.ResultUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    DashboardDao dashboardDao;

    @Autowired
    ResTreeDao resTreeDao;

    @Autowired
    ResourceTreeService resourceTreeService;

    @Override
    public Result<Dashboard> getDashboardByResId(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Dashboard dashboard = dashboardDao.getDashboardByResId(resId);
        if (dashboard == null) {
            return ResultUtils.buildResult(ResultCode.DASHBOARD_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, dashboard);
    }

    @Override
    public Result addDashboard(DashboardVO dashboardVO) {
        if (dashboardVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Dashboard dashboard = new Dashboard();
        String resId = CommonUtils.getUUID();
        dashboard.setResId(resId);
        dashboard.setName(dashboardVO.getName());
        dashboard.setLayoutJson(dashboardVO.getLayoutJson());
        if (!dashboardDao.addDashboard(dashboard)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        Map<String, Object> param = new HashMap<>(5);
        param.put("resId", dashboard.getResId());
        param.put("name", dashboard.getName());
        param.put("pid", dashboardVO.getPid());
        param.put("type", ResourceType.DASHBOARD.value());
        param.put("businessLine", dashboardVO.getBusinessLine());
        return resourceTreeService.addNode(param);
    }

    @Override
    public Result updateDashboard(Dashboard dashboard) {
        if (dashboard == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!dashboardDao.updateDashboard(dashboard)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", dashboard.getName());
        return resourceTreeService.updateNode(dashboard.getResId(), param);
    }

}
