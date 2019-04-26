package com.yiche.bigdata;

import com.alibaba.fastjson.JSON;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.service.AuthenticationFeignService;
import com.yiche.bigdata.service.core.UserContextContainer;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthenticationFeignService authenticationFeignService;

    @Autowired
    private UserContextContainer userContextContainer;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String token = TokenUtils.getToken(httpServletRequest);
        Result<Integer> result = new Result<>();
        int retryCount = 10;
        if(StringUtils.isEmpty(token)){
            result.setCodeAndMsg(ResultCode.EX_TOKEN_OVERDUE);
        }else {
            Result verifyResult;
            while (retryCount > 0){
                retryCount--;
                try {
                    verifyResult = authenticationFeignService.verifyToken(token, httpServletRequest.getRequestURL().toString());
                }catch (Exception e){
                    LOGGER.error("Validate Token Failure : " + token, e);
                    if(retryCount == 0){
                        result.setCodeAndMsg(ResultCode.AUTHENTICATION_SERVICE_ERROR);
                    }
                    continue;
                }
                if(verifyResult != null){
                    if(verifyResult.getCode() == 0){
                        userContextContainer.getUserContext(token);
                        return true;
                    }else{
                        result.setCodeAndMsg(ResultCode.EX_TOKEN_OVERDUE);
                        break;
                    }
                }
            }
        }
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(result));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
