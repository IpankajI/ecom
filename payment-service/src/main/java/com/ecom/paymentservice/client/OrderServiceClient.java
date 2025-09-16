package com.ecom.paymentservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.paymentservice.dto.OrderResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {
    private final WebClient webClient;
    private static final String ORDER_SERVICE_ENDPOINT="http://order-service:30003/api/orders/";
    public OrderResponse getOrderById(String orderId) {
        return webClient
            .get()
            .uri(ORDER_SERVICE_ENDPOINT+orderId)
            .retrieve()
            .bodyToMono(OrderResponse.class)
            .block();
    }
}
