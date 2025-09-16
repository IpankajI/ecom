package com.ecom.orderservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.orderservice.dto.Product;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ProductServiceClient {
    private final WebClient webClient;
    private static final String PRODUCT_SERVICE_ENDPOINT="http://product-service:30001/api/products/";

    public Product getProductById(String id){
                
        return webClient.get()
            .uri(PRODUCT_SERVICE_ENDPOINT+id)
            .retrieve()
            .bodyToMono(Product.class)
            .block();

    }

}
