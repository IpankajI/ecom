package com.ecom.inventoryservice.appconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecom.inventoryservice.utils.IDGenerator;
import com.ecom.inventoryservice.utils.IDGeneratorRandom;

@Configuration
public class AppConfig {
    @Value("${app.config.inventory-claim-expiry-in-minutes}")
	public long inventoryClaimExpiryInMinutes;

    @Bean
    public IDGenerator idGenerator(){
        return new IDGeneratorRandom();
    }
}
