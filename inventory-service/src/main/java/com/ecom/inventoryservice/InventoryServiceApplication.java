package com.ecom.inventoryservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.inventoryservice.model.Inventory;
import com.ecom.inventoryservice.repository.InventoryRepository;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}


	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){

		return args ->{
			Inventory inventory=new Inventory(null, "iphone_11", 0);
			inventoryRepository.save(inventory);

			inventory=new Inventory(null, "iphone_12", 10);
			inventoryRepository.save(inventory);
		};

	}

	@Bean
	public WebClient webClient(){
		return WebClient.builder().build();
	}

}
