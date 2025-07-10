package com.ecom.productservice.appconfig;

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
}
