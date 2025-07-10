package com.ecom.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecom.userservice.utils.IDGenerator;
import com.ecom.userservice.utils.IDGeneratorRandom;

@Configuration
public class AppConfig {

    @Bean
    public IDGenerator idGenerator(){
        return new IDGeneratorRandom();
    }
}
