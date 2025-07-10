package com.ecom.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemRequest {
    private Long inventoryId;
    private Integer quantity;
}
