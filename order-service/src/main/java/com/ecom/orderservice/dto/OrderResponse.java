package com.ecom.orderservice.dto;

import java.math.BigDecimal;
import java.util.List;

import com.ecom.orderservice.model.OrderPaymentStatus;
import com.ecom.orderservice.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private List<OrderLineItemResponse> orderLineItemResponses;
    private BigDecimal totalAmount;
    private OrderPaymentStatus paymentStatus;
    private OrderStatus status;
}
