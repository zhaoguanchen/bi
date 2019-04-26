package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.DatasourcePermission;
import com.yiche.bigdata.entity.generated.DatasourcePermissionExample;
import com.yiche.bigdata.mapper.generated.DatasourcePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class DatasourcePermissionDao {

    @Autowired
    private DatasourcePermissionMapper datasourcePermissionMapper;

    public boolean save(DatasourcePermission datasourcePermission) {
        return datasourcePermissionMapper.insertSelective(datasourcePermission) > 0;
    }

    public List<DatasourcePermission> findByBusinessLine(String businessLine) {
        DatasourcePermissionExample example = new DatasourcePermissionExample();
        example.createCriteria().andBusinessLineEqualTo(businessLine);
        return datasourcePermissionMapper.selectByExample(example);
    }

    public DatasourcePermission findOne(String dataSourceId, String businessLine) {
        DatasourcePermissionExample example = new DatasourcePermissionExample();
        example.createCriteria().andBusinessLineEqualTo(businessLine).andDatasourceIdEqualTo(dataSourceId);
        List<DatasourcePermission> list = datasourcePermissionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public boolean delete(String dataSourceId, String businessLine) {
        DatasourcePermissionExample example = new DatasourcePermissionExample();
        example.createCriteria().andBusinessLineEqualTo(businessLine).andDatasourceIdEqualTo(dataSourceId);
        return datasourcePermissionMapper.deleteByExample(example) > 0;
    }

    public boolean upadate(DatasourcePermission datasourcePermission) {
        return datasourcePermissionMapper.updateByPrimaryKey(datasourcePermission) > 0;
    }
}
