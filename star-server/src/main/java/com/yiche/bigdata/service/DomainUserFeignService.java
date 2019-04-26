package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "uap-user-service")
public interface DomainUserFeignService {

    /**
     *
     * 根据域账号查询域账号信息
     *
     * @param username

     * @return Result<DomainUserInfo>.class
     */
    @RequestMapping(value = "/uap/user/search/domain/{username}", method = RequestMethod.GET)
    Result<DomainUserInfo> getDomainUserInfo(@PathVariable("username") final String username);


    /**
     *
     * 模糊搜索域账号
     *
     * @param username

     * @return Result<List<DomainUserInfo>>.class
     */
    @RequestMapping(value = "/uap/user/fuzzy/search/cached/domain", method = RequestMethod.GET)
    Result<List<DomainUserInfo>> searchDomainUser(@RequestParam("username") final String username,
                                                  @RequestParam("size") final Integer size);
}
