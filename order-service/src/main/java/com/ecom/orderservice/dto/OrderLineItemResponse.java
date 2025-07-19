package com.ecom.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemResponse {
    private String id;
    private String totalAmount;
    private Integer quantity;   
    private String productId;
}
