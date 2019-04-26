package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.service.AuthenticationFeignService;
import com.yiche.bigdata.service.DomainUserFeignService;
import com.yiche.bigdata.service.UserInfoService;
import com.yiche.bigdata.service.core.ResourceCenter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private AuthenticationFeignService authenticationFeignService;

    @Autowired
    private DomainUserFeignService domainUserFeignService;

    @Autowired
    private ResourceCenter resourceCenter;

    @Value("${star.domain.account.search.count:3}")
    private Integer searchCount;

    @Override
    public String getUserNameByToken(String token) {
        return authenticationFeignService.getLoginUser(token);
    }

    @Override
    public DomainUserInfo getUserInfoByUserName(String username) {
        if (StringUtils.isNotEmpty(username)) {
            return resourceCenter.getUserInfo(username);
        }
        return null;
    }

    @Override
    public List<DomainUserInfo> searchDomainUser(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        Result<List<DomainUserInfo>> result = domainUserFeignService.searchDomainUser(username, searchCount);
        if (result.getCode() != ResultCode.OK.value()) {
            return null;
        }
        List<DomainUserInfo> resultList = result.getResult();
        if (!CollectionUtils.isEmpty(resultList)) {
            return resultList;
        }
        return null;
    }
}
