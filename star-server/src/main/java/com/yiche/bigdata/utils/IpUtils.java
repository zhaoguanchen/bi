package com.yiche.bigdata.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = StringUtils.EMPTY;

        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                ip = XFor.substring(0, index);
            } else {
                ip = XFor;
            }
        }
        if (StringUtils.isNotEmpty(Xip) && !"unKnown".equalsIgnoreCase(Xip)) {
            ip = Xip;
        }
        if (StringUtils.isBlank(Xip) || "unknown".equalsIgnoreCase(Xip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getRemoteIp() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return getRemoteIp(request);
    }
}
