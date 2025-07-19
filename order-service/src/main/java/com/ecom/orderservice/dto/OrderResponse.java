package com.ecom.orderservice.dto;

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
    private String id;
    private String orderNumber;
    private List<OrderLineItemResponse> orderLineItemResponses;
    private String totalAmount;
    private OrderPaymentStatus paymentStatus;
    private OrderStatus status;
}
