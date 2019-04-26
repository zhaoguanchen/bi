package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "uap-sso-service")
public interface AuthenticationFeignService {

    /**
     * 通过token获取用户名
     * @param token
     * @return
     */
    @RequestMapping(value = "/uap/sso/v2/principal/{token}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getLoginUser(@PathVariable("token") String token);

    /**
     * 通过token获取信息，{未知：接口提供方。。。}
     * @param token
     * @param service
     * @return
     */
    @RequestMapping(value = "/uap/sso/v3/tickets/{token}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Result verifyToken(@PathVariable("token") String token, @RequestParam("service") String service);

}
