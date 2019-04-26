package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.Dataset;
import com.yiche.bigdata.entity.generated.DatasetExample;
import com.yiche.bigdata.mapper.generated.DatasetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class DatasetDao {

    @Autowired
    DatasetMapper datasetMapper;

    public Dataset getDatasetByResId(String resId) {
        return datasetMapper.selectByPrimaryKey(resId);
    }

    public List<Dataset> getDatasetByResIds(List<String> resIds) {
        if (CollectionUtils.isEmpty(resIds)) {
            return null;
        }
        DatasetExample example = new DatasetExample();
        example.createCriteria().andResIdIn(resIds);
        return datasetMapper.selectByExampleWithBLOBs(example);
    }


    public boolean addDataTable(Dataset dataset) {
        int result = datasetMapper.insert(dataset);
        return result > 0;
    }

    public boolean updateDataset(Dataset dataset) {
        int result = datasetMapper.updateByPrimaryKeyWithBLOBs(dataset);
        return result > 0;
    }

    public boolean deleteDataset(String resId) {
        int result = datasetMapper.deleteByPrimaryKey(resId);
        return result > 0;
    }

}
