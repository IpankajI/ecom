package com.ecom.orderservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemRequest {
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
