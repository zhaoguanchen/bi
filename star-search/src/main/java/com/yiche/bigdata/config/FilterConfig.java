package com.yiche.bigdata.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Created by Lenovo on 2018/1/31.
 */
//@Configuration
public class FilterConfig {
    @Bean(name = "encodingFilter")
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CharacterEncodingFilter());
        filterRegistrationBean.addInitParameter("encoding","UTF-8");
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
    @Bean(name = "springmvc")
    public ServletRegistrationBean servletRegistrationBean1(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new DispatcherServlet());
        servletRegistrationBean.addInitParameter("contextClass","org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
        servletRegistrationBean.addInitParameter("contextConfigLocation","com.yiche.bigdata.controller.WebConfig");
        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.getUrlMappings().clear();
        //servletRegistrationBean.addUrlMappings("*.do");
        return servletRegistrationBean;
    }

}
