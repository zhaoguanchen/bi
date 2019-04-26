package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.generated.DatasourceExample;
import com.yiche.bigdata.mapper.generated.DatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatasourceDao {

    @Autowired
    DatasourceMapper datasourceMapper;

    public List<Datasource> getDatasourceList() {
        DatasourceExample example = new DatasourceExample();
        example.setOrderByClause("create_time asc");
        return datasourceMapper.selectByExampleWithBLOBs(example);
    }

    public Datasource getDatasourceByResId(String resId) {
        return datasourceMapper.selectByPrimaryKey(resId);
    }

    public boolean addDataTable(Datasource Datasource) {
        int result = datasourceMapper.insert(Datasource);
        return result > 0;
    }

    public boolean updateDatasource(Datasource Datasource) {
        int result = datasourceMapper.updateByPrimaryKeyWithBLOBs(Datasource);
        return result > 0;
    }

    public boolean deleteDatasource(String resId) {
        int result = datasourceMapper.deleteByPrimaryKey(resId);
        return result > 0;
    }
}
