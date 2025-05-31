package com.ecom.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.inventoryservice.model.InventoryOperation;

public interface InventoryOperationRepository extends JpaRepository<InventoryOperation, String> {
    InventoryOperation getByInventoryId(Long inventoryId);
}

