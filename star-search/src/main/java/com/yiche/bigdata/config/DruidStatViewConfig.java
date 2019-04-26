package com.yiche.bigdata.config;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Lenovo on 2018/1/31.
 */
//@Configuration
public class DruidStatViewConfig {
    @Bean(name = "DruidStatView")
    public ServletRegistrationBean servletRegistrationBean (){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet());
        servletRegistrationBean.addInitParameter("loginUsername","admin");
        servletRegistrationBean.addInitParameter("loginPassword","root123");
        servletRegistrationBean.addUrlMappings("/druid/*");
        return servletRegistrationBean;
    }

}
