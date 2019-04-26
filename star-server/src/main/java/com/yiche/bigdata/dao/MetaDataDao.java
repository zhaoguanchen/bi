package com.yiche.bigdata.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiche.bigdata.entity.dto.ApiResponse;
import com.yiche.bigdata.entity.pojo.MetadataTableInfo;
import com.yiche.bigdata.utils.HttpContentUtils;
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

@Component
public class MetaDataDao {

    private static final Logger logger = LoggerFactory.getLogger(MetaDataDao.class);

    @Value("${metadata.base.url}")
    private String metadataServiceUrl;

    @Value("${metadata.queryTable.url}")
    private String metadataQueryTableUrl;

    public List<MetadataTableInfo> getAllAvailableTableInfo() {
        String requestUrl = metadataServiceUrl+metadataQueryTableUrl;
        try {
            HttpResponse response = Request.Get(requestUrl).execute().returnResponse();
            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                logger.error("get all table info access error, url {}, method {}", requestUrl, RequestMethod.POST);
                throw new RuntimeException("get all table info from metadata service");
            }
            ObjectMapper mapper = HttpContentUtils.getObjectMapper();
            String result = HttpContentUtils.getResponseStr(response);
            ApiResponse apiResponse = mapper.readValue(result, ApiResponse.class);
            List<Object> objectList = (List<Object>) apiResponse.getData();
            List<MetadataTableInfo> resultList = objectList.stream()
                    .map(objectTemp -> mapper.convertValue(objectTemp, MetadataTableInfo.class))
                    .collect(Collectors.toList());
            return resultList.stream()
                    .filter(metadataTableInfo -> metadataTableInfo.getBizType() == 1)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("get all table info access error, url {}, method {}", requestUrl, RequestMethod.POST, e);
            throw new RuntimeException(" get all table info from  metadata  service", e);
        }
    }

}
