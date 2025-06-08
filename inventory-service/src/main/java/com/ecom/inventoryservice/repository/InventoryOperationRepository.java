package com.ecom.inventoryservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecom.inventoryservice.model.InventoryOperation;

public interface InventoryOperationRepository extends JpaRepository<InventoryOperation, String> {
    InventoryOperation getByInventoryId(Long inventoryId);

    @Query(value = "select * from inventory_operations where inventory_operation_status = ?1 and inventory_operation_type = ?2 order by updated_at limit ?3", nativeQuery = true)
    List<InventoryOperation> findAllExpiredInventory(String inventoryOperationStatus, String inventoryOperationType, int limit);
}

