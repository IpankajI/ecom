package com.ecom.orderservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemResponse {
    private Long id;
    private String skuCode;
    private BigDecimal totalAmount;
    private Integer quantity;   
    private String productId;
}
