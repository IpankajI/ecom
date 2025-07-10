package com.ecom.inventoryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Inventory {
    @Id
    private Long id;
    private String skuCode;
    private Integer quantity;
    private String productId;
    private Long storeId;
}
