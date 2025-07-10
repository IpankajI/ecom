package com.ecom.inventoryservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "inventory_operations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InventoryOperation {
    @Id
    private Long operationId;
    @Enumerated(EnumType.STRING)
    private InventoryOperationType inventoryOperationType;
    @Enumerated(EnumType.STRING)
    private InventoryOperationStatus inventoryOperationStatus;
    private Integer quantity;
    private Long inventoryId;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
}
