package com.ecom.productservice.appconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecom.productservice.utils.IDGenerator;
import com.ecom.productservice.utils.IDGeneratorRandom;

@Configuration
public class AppConfig {

    @Bean
    public IDGenerator idGenerator(){
        return new IDGeneratorRandom();
    }
    @Bean
    public Logger logger(){
        return LoggerFactory.getLogger(AppConfig.class);
    }
}
