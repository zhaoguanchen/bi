package com.yiche.bigdata.dao;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiche.bigdata.dto.EnumTableDto;
import com.yiche.bigdata.dto.NodeDetail;
import com.yiche.bigdata.pojo.ApiResponse;
import com.yiche.bigdata.pojo.MetadataTableInfo;
import com.yiche.bigdata.pojo.TableDetail;
import com.yiche.bigdata.util.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jmy on 2017/9/26.
 */
@Component
public class MetadataDao {

    private static final Logger logger = LoggerFactory.getLogger(MetadataDao.class);

    @Value("${metadata.base.url}")
    private String metadataServiceUrl;

    @Value("${metadata.queryTable.url}")
    private String metadataQueryTableUrl;

    @Value("${metadata.queryTableDetail.url}")
    private String metadataQueryTableDetailUrl;

    @Value("${metadata.queryEnumInfo.url}")
    private String metadataEnumInfoUrl;

    @Value("${metadata.queryNodeDetail.url}")
    private String metadataNodeDetailUrl;

    private static final int BI_METADATA_TABLE_BIZTYPE = 1;


    public List<MetadataTableInfo> getAllAvailableTableInfo() {
        StringBuilder requestUrlBuilder = new StringBuilder(metadataServiceUrl);
        requestUrlBuilder.append(metadataQueryTableUrl);
        try {
            HttpResponse response = Request.Get(requestUrlBuilder.toString()).execute().returnResponse();

            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                logger.error("get all table info access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST);
                throw new RuntimeException("get all table info from metadata service");
            }

            ObjectMapper mapper = Utils.getObjectMapper();
            String result = Utils.getResponseStr(response);
            ApiResponse apiResponse = mapper.readValue(result, ApiResponse.class);
            List<Object> objectList = (List<Object>) apiResponse.getData();
            List<MetadataTableInfo> resultList =
                    objectList.stream().map(objectTemp -> mapper.convertValue(objectTemp, MetadataTableInfo.class)).collect(Collectors.toList());
            // 过滤可用table
            return resultList.stream().filter(metadataTableInfo -> metadataTableInfo.getBizType() ==BI_METADATA_TABLE_BIZTYPE).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("get all table info access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST, e);
            throw new RuntimeException(" get all table info from  metadata  service", e);
        }
    }

    public TableDetail getTableDetailByName(String tableName) {
        StringBuilder requestUrlBuilder = new StringBuilder(metadataServiceUrl);
        requestUrlBuilder.append(metadataQueryTableDetailUrl);
        requestUrlBuilder.append("?table=").append(tableName);
        try {
            HttpResponse response = Request.Get(requestUrlBuilder.toString()).execute().returnResponse();

            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                logger.error("getTableDetailByName  access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST);
                throw new RuntimeException("getTableDetailByName from metadata service");
            }

            ObjectMapper mapper = Utils.getObjectMapper();
            String result = Utils.getResponseStr(response);
            ApiResponse apiResponse = mapper.readValue(result, ApiResponse.class);

            return mapper.convertValue(apiResponse.getData(), TableDetail.class);
        } catch (IOException e) {
            logger.error("getTableDetailByName access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST, e);
            throw new RuntimeException(" getTableDetailByName from  metadata  service", e);
        }
    }

    public List<EnumTableDto> getEnumTableInfoByPrefix(String prefixName){
        StringBuilder requestUrlBuilder = new StringBuilder(metadataServiceUrl);
        requestUrlBuilder.append(metadataEnumInfoUrl);
        requestUrlBuilder.append("?prefix=").append(prefixName);
        try {
            HttpResponse response = Request.Get(requestUrlBuilder.toString()).execute().returnResponse();

            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                logger.error("getEnumTableInfoByPrefix  access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST);
                throw new RuntimeException("getEnumTableInfoByPrefix from metadata service");
            }

            ObjectMapper mapper = Utils.getObjectMapper();
            String result = Utils.getResponseStr(response);
            ApiResponse apiResponse = mapper.readValue(result, ApiResponse.class);
            List<Object> objectList = (List<Object>) apiResponse.getData();

            List<EnumTableDto> resultList =
                    objectList.stream().map(objectTemp -> mapper.convertValue(objectTemp, EnumTableDto.class)).collect(Collectors.toList());
            return resultList;
        } catch (IOException e) {
            logger.error("getEnumTableInfoByPrefix access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST, e);
            throw new RuntimeException(" getEnumTableInfoByPrefix from  metadata  service", e);
        }
    }



    public NodeDetail getNodeDetailByNodeKey(String nodeKey){
        StringBuilder requestUrlBuilder = new StringBuilder(metadataServiceUrl);
        requestUrlBuilder.append(metadataNodeDetailUrl);
        requestUrlBuilder.append("?nodeKey=").append(nodeKey);
        try {
            HttpResponse response = Request.Get(requestUrlBuilder.toString()).execute().returnResponse();

            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                logger.error("getNodeDetailByPrefixByNodeKey  access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST);
                throw new RuntimeException("getNodeDetailByPrefixByNodeKey from metadata service");
            }

            ObjectMapper mapper = Utils.getObjectMapper();
            String result = Utils.getResponseStr(response);
            ApiResponse apiResponse = mapper.readValue(result, ApiResponse.class);

            return mapper.convertValue(apiResponse.getData(), NodeDetail.class);
        } catch (IOException e) {
            logger.error("getNodeDetailByPrefixByNodeKey access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST, e);
            throw new RuntimeException(" getNodeDetailByPrefixByNodeKey from  metadata  service", e);
        }
    }

}
