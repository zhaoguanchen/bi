package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.Report;
import com.yiche.bigdata.mapper.generated.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportDao {

    @Autowired
    ReportMapper reportMapper;

    public Report getReportByResId(String resId) {
        return reportMapper.selectByPrimaryKey(resId);
    }

    public boolean addDataTable(Report report) {
        int result = reportMapper.insert(report);
        return result > 0;
    }

    public boolean updateReport(Report report) {
        int result = reportMapper.updateByPrimaryKeyWithBLOBs(report);
        return result > 0;
    }

    public boolean deleteReport(String resId) {
        int result = reportMapper.deleteByPrimaryKey(resId);
        return result > 0;
    }


    public boolean countByDateSetId(String dataSetId) {
        int result = reportMapper.countByDateSetId(dataSetId);
        return result > 0;
    }

}
