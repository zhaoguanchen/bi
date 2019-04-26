package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.DataSourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.DatasourceDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.Datasource;
import com.yiche.bigdata.entity.vo.BusinessLineVo;
import com.yiche.bigdata.entity.vo.DatasourceVO;
import com.yiche.bigdata.service.DatasourceService;
import com.yiche.bigdata.service.UserInfoService;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.EnumUtils;
import com.yiche.bigdata.utils.ResultUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    DatasourceDao datasourceDao;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public Result<List<Datasource>> getDatasourceList() {
        List<Datasource> datasourceList = datasourceDao.getDatasourceList();
        if (CollectionUtils.isEmpty(datasourceList)) {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, datasourceList);
    }

    @Override
    public Result getDatasourceByResId(String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Datasource datasource = datasourceDao.getDatasourceByResId(id);
        if (datasource == null) {
            return ResultUtils.buildResult(ResultCode.DATASOURCE_NOT_EXIST);
        }
        return ResultUtils.buildResult(ResultCode.OK, datasource);
    }

    @Override
    public Result addDatasource(DatasourceVO datasourceVO) {
        if (datasourceVO == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Datasource datasource = new Datasource();
        String resId = CommonUtils.getUUID();
        datasource.setId(resId);
        datasource.setName(datasourceVO.getName());
        DataSourceType typeEnum = EnumUtils.valueOf(DataSourceType.class, datasourceVO.getType());
        datasource.setType(typeEnum.value());
        datasource.setConfig(datasourceVO.getConfig());
        datasource.setDescription(datasourceVO.getDescription());
        datasource.setCreater(userInfoService.getUserNameByToken(TokenUtils.getToken()));
        datasource.setCreateTime(new Date());
        datasource.setLastModifier(userInfoService.getUserNameByToken(TokenUtils.getToken()));
        datasource.setLastModifyTime(new Date());
        if (!datasourceDao.addDataTable(datasource)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        return ResultUtils.buildResult(ResultCode.OK);
    }

    @Override
    public Result updateDatasource(Datasource datasource) {
        if (datasource == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Datasource older = datasourceDao.getDatasourceByResId(datasource.getId());
        older.setLastModifier(userInfoService.getUserNameByToken(TokenUtils.getToken()));
        older.setLastModifyTime(new Date());
        older.setConfig(datasource.getConfig());
        older.setName(datasource.getName());
        older.setDescription(datasource.getDescription());
        older.setType(datasource.getType());
        if (!datasourceDao.updateDatasource(older)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        return ResultUtils.buildResult(ResultCode.OK);
    }

    @Override
    public Result deleteDatasource(String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        if (!datasourceDao.deleteDatasource(id)) {
            return ResultUtils.buildResult(ResultCode.DATA_PERSISTENCE_FAILURE);
        }
        return ResultUtils.buildResult(ResultCode.OK);
    }

}
