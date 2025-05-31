package com.ecom.inventoryservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecom.inventoryservice.model.Inventory;
import com.ecom.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String skuCode){

        Optional<Inventory> inventory=inventoryRepository.findBySkuCode(skuCode);
        if (!inventory.isPresent() ){
            return false;
        }

        return inventory.isPresent() && inventory.get().getQuantity()>0;
    }

}
