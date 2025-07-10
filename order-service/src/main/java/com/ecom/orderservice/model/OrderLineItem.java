package com.ecom.orderservice.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_line_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItem {
    
    @Id
    private Long id;
    private String skuCode;
    private BigDecimal totalAmount;
    private Integer quantity;
    @ManyToOne
    private Order order;
    @Column(nullable = false)
    private Long inventoryId;
    private Long inventoryClaimId;
}
