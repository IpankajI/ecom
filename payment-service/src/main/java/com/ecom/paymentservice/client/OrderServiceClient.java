package com.ecom.paymentservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.paymentservice.dto.OrderResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {
    private final WebClient webClient;
    public OrderResponse getOrderById(String orderId) {
        return webClient
            .get()
            .uri("http://order-service:30003/api/orders/"+orderId)
            .retrieve()
            .bodyToMono(OrderResponse.class)
            .block();
    }
}
