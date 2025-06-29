package com.ecom.paymentservice.appconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AppConfig {
    

    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }
}
