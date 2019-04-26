package com.yiche.bigdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Lenovo on 2018/1/30.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.yiche.bigdata"})
@ImportResource(locations= {"classpath:spring-cacher-ehcache.xml"})

public class StarSearchApplication extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(StarSearchApplication.class, args);
    }
}
