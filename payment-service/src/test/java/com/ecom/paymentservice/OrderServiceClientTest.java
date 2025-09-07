package com.ecom.paymentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ecom.paymentservice.client.OrderServiceClient;
import com.ecom.paymentservice.dto.OrderResponse;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class OrderServiceClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RequestHeadersSpec requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private OrderServiceClient orderServiceClient;

    private OrderResponse mockOrderResponse;

    @BeforeEach
    void setUp() {
        mockOrderResponse = OrderResponse.builder()
            .id(123L)
            .totalAmount(BigDecimal.valueOf(50))
            .status("COMPLETED")
            .build();
    }

    @Test
    void shouldReturnOrderResponse_whenSuccessful() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OrderResponse.class)).thenReturn(Mono.just(mockOrderResponse));

        OrderResponse result = orderServiceClient.getOrderById("123");

        assertEquals(mockOrderResponse.getId(), result.getId());
        assertEquals(mockOrderResponse.getTotalAmount(), result.getTotalAmount());
        assertEquals(mockOrderResponse.getStatus(), result.getStatus());
    }

    @Test
    void shouldThrowException_whenOrderIsNotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OrderResponse.class))
            .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        assertThrows(WebClientResponseException.class, () -> {
            orderServiceClient.getOrderById("order456");
        });
    }

    @Test
    void shouldThrowException_whenNetworkErrorOccurs() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
            .thenThrow(WebClientRequestException.class); // Simulate a network error

        assertThrows(WebClientRequestException.class, () -> {
            orderServiceClient.getOrderById("order789");
        });
    }
}
