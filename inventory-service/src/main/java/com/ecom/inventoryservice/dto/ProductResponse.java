package com.ecom.inventoryservice.dto;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    
}