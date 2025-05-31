package com.ecom.inventoryservice.appconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${app.config.inventory-claim-rxpiry-in-minutes}")
	public long inventoryClaimExpiryInMinutes;
}
