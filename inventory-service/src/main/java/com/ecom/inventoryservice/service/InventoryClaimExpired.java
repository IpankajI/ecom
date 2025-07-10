package com.ecom.inventoryservice.service;


import java.time.LocalDateTime;
import java.util.List;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.inventoryservice.appconfig.AppConfig;
import com.ecom.inventoryservice.model.Inventory;
import com.ecom.inventoryservice.model.InventoryOperation;
import com.ecom.inventoryservice.model.InventoryOperationStatus;
import com.ecom.inventoryservice.model.InventoryOperationType;
import com.ecom.inventoryservice.repository.InventoryOperationRepository;
import com.ecom.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class InventoryClaimExpired {
    private final InventoryOperationRepository inventoryOperationRepository;
    private final InventoryRepository inventoryRepository;
    private final AppConfig appConfig;

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void run(){

        List<InventoryOperation> ops=inventoryOperationRepository.findAllExpiredInventory(InventoryOperationStatus.InventoryOperationStatusInitiated.name(), 
                                InventoryOperationType.InventoryOperationTypeClaim.name(), 10_000);

        for(InventoryOperation inventoryOperation:ops){
            try {
                reclaimExpiredInventory(inventoryOperation.getInventoryId(), inventoryOperation.getOperationId());
            } catch (Exception e) {
                System.out.println("...... "+e.getMessage());
            }
        }

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void reclaimExpiredInventory(long inventoryId, long operationId){

        // get lock on inventory
        Inventory inventory=inventoryRepository.findById(inventoryId).get();
        if(inventory==null){
            throw new RuntimeCryptoException("inventory not found");
        }
        InventoryOperation inventoryOperation=inventoryOperationRepository.findById(operationId).get();
        if(inventoryOperation==null){
            throw new RuntimeException("no such operation with id: "+operationId);
        }
        if(inventoryOperation.getInventoryOperationType()!=InventoryOperationType.InventoryOperationTypeClaim
            || inventoryOperation.getInventoryOperationStatus()!=InventoryOperationStatus.InventoryOperationStatusInitiated){
            throw new RuntimeException("invalid operation");
        }
        if(!inventoryOperation.getCreateAt().isBefore(LocalDateTime.now().minusMinutes(appConfig.inventoryClaimExpiryInMinutes))){
            return;
        }

        // claim expired
        inventoryOperation.setInventoryOperationStatus(InventoryOperationStatus.InventoryOperationStatusExpired);
        inventory.setQuantity(inventory.getQuantity()+inventoryOperation.getQuantity());

        inventoryOperationRepository.save(inventoryOperation);
        inventoryRepository.save(inventory);

    }
}



// @Getter
// class Product {
//     private String id;
//     private String name;
//     private String description;
//     private BigDecimal price;
    
// }
