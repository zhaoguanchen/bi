package com.yiche.bigdata.services;

import com.yiche.bigdata.dao.MetadataDao;
import com.yiche.bigdata.dto.EnumTableDto;
import com.yiche.bigdata.dto.NodeDetail;
import com.yiche.bigdata.pojo.MetadataTableInfo;
import com.yiche.bigdata.pojo.TableDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jmy on 2017/9/29.
 */
@Repository
public class MetadataService {


    @Autowired
    private MetadataDao metadataDao;

//    @Autowired
//    private AuthenticationService authenticationService;

    public List<MetadataTableInfo> getTableInfoList() {
        return this.metadataDao.getAllAvailableTableInfo();
    }

    public TableDetail getTableDetail(String tableName) {
        return this.metadataDao.getTableDetailByName(tableName);
    }

    public List<EnumTableDto> getEnumTableInfoByNodeKey(String nodeKey) {
        NodeDetail nodeDetail = this.metadataDao.getNodeDetailByNodeKey(nodeKey);
        if (nodeDetail != null && nodeDetail.getDataDictTable() != null && !nodeDetail.getDataDictTable().isEmpty()) {
            if (nodeDetail.getDataDictTable().equalsIgnoreCase("carmodel")) {
                List<EnumTableDto> carModelEnumList = this.metadataDao.getEnumTableInfoByPrefix(nodeDetail.getDataDictTable());
                return carModelEnumList.stream().map(enumTableDto -> {
                    enumTableDto.setName(enumTableDto.getName().concat("(").concat(enumTableDto.getId().substring(9).concat(")")));
                    return enumTableDto;
                }).collect(Collectors.toList());
            }else{
                return this.metadataDao.getEnumTableInfoByPrefix(nodeDetail.getDataDictTable());
            }
        }
        return null;
    }

    public NodeDetail getNodeDetailByNodeKey(String nodeKey){
        return metadataDao.getNodeDetailByNodeKey(nodeKey);
    }
}
