package com.ecom.paymentservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String status;
}
