package com.yiche.bigdata.dao;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.LinkedHashMap;

/**
 * Created by jmy on 2017/9/26.
 */
@Component
public class UserCheckDao {

    private static final Logger logger = LoggerFactory.getLogger(UserCheckDao.class);

    @Value("${user.check.url}")
    private String userCheckUrl;


    /*public String checkUserNameLegal(String userName) {
        StringBuilder requestUrlBuilder = new StringBuilder(userCheckUrl);
        try {
            requestUrlBuilder.append(userName);
            HttpResponse response = Request.Get(requestUrlBuilder.toString()).execute().returnResponse();

            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                logger.error("check user name legal access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST);
                throw new RuntimeException("check user name legal from user service");
            }

            ObjectMapper mapper = Utils.getObjectMapper();
            String result = Utils.getResponseStr(response);
            ApiResult apiResult = mapper.readValue(result, ApiResult.class);
            if (apiResult.getErrorNo() == 0 && apiResult.getResult() != null) {
                LinkedHashMap<String, String> userMap = (LinkedHashMap<String, String>) apiResult.getResult();
                DomainUserInfo domainUserInfo = new DomainUserInfo();
                domainUserInfo.setName(userMap.get("name"));
                domainUserInfo.setUserId(userMap.get("userId"));
                domainUserInfo.setSeat(userMap.get("seat"));
                domainUserInfo.setDepartment(userMap.get("department"));
                domainUserInfo.setPosition(userMap.get("position"));
                return JSONObject.toJSONString(domainUserInfo);
            } else {
                return "0";
            }
        } catch (IOException e) {
            logger.error("check user name legal access error, url {}, method {}", requestUrlBuilder.toString(), RequestMethod.POST, e);
            throw new RuntimeException(" check user name legal from  user  service", e);
        }
    }*/
}
