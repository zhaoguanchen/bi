package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.DataTable;
import com.yiche.bigdata.entity.generated.DataTableExample;
import com.yiche.bigdata.mapper.generated.DataTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataTableDao {

    @Autowired
    private DataTableMapper dataTableMapper;

    public boolean addDataTable(DataTable dataTable) {
        int result = dataTableMapper.insert(dataTable);
        return result > 0;
    }

    public DataTable findDataTableById(String resId) {
        return dataTableMapper.selectByPrimaryKey(resId);
    }

    public boolean deleteById(String resId) {
        return dataTableMapper.deleteByPrimaryKey(resId) > 0;
    }

    public List<DataTable> findByIds(List<String> ids) {
        DataTableExample example = new DataTableExample();
        example.createCriteria().andResIdIn(ids);
        return dataTableMapper.selectByExample(example);
    }

}
