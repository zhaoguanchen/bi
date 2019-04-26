package com.yiche.bigdata.dao;

import com.yiche.bigdata.pojo.DashboardLog;
import org.springframework.stereotype.Repository;

/**
 * Created by root on 2018/4/20.
 */
@Repository
public interface DashboardLogDao {
    int save(DashboardLog dashboardLog);
}
