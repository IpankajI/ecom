package com.ecom.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.orderservice.utils.IDGeneratorRandom;
import com.ecom.orderservice.utils.IDGenerator;

@Component
public class AppConfig {
    @Bean
	public WebClient webClient(){


		return WebClient.builder().build();
	}
	
	@Bean
    public IDGenerator idGenerator(){
        return new IDGeneratorRandom();
    }
}
