package com.ecom.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.inventoryservice.model.InventoryOperationType;


@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}


	@Bean
	public WebClient webClient(){

		System.out.println(".......... "+InventoryOperationType.InventoryOperationTypeClaim);

		return WebClient.builder().build();
	}

}
