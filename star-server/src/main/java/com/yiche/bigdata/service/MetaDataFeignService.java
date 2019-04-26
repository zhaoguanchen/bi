package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.ApiResponse;
import com.yiche.bigdata.entity.pojo.MetadataTableInfo;
import com.yiche.bigdata.entity.pojo.TableDetail;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 调用元数据服务 
 */
@FeignClient(value = "metadata")
public interface MetaDataFeignService {

   /**
    * 获取所有的元数据库表信息
    * @return ApiResponse<List<MetadataTableInfo>>.class
    */
   @RequestMapping(value = "/query/tableInfo/total", method = RequestMethod.GET)
   ApiResponse<List<MetadataTableInfo>> getAllAvailableTableInfo();

   /**
    * 通过表名获取库表的详细信息
    * @param tableName
    * @return ApiResponse<TableDetail>.class
    */
   @RequestMapping(value = "/query/tableDetail/byTableName", method = RequestMethod.GET)
   ApiResponse<TableDetail> getTableDetail(@RequestParam("table") String tableName);
}


