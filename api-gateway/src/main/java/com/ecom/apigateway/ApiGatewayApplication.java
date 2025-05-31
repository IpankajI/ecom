package com.ecom.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class ApiGatewayApplication {



	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}


}
