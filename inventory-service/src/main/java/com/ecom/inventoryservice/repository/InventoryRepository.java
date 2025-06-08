package com.ecom.inventoryservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.ecom.inventoryservice.model.Inventory;

import jakarta.persistence.LockModeType;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
    Optional<Inventory> findBySkuCode(String skuCode);

    // @Query("SELECT * FROM inventories WHERE product_id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Inventory getByProductId(String productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findById(Long id);

}
