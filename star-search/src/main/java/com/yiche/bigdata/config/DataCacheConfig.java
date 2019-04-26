package com.yiche.bigdata.config;

import com.yiche.bigdata.cache.EhCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataCacheConfig {

    @Bean(name = "rawDataCache")
    public EhCacheManager ehCacheManagerBean() {
        EhCacheManager cacheManager = new EhCacheManager<>();
        cacheManager.setCacheAlias("jvmAggregator");
        return cacheManager;
    }
}
