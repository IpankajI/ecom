package com.ecom.inventoryservice.service;


import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
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
// @EnableScheduling
@RequiredArgsConstructor
public class InventoryClaimExpired {
    private final InventoryOperationRepository inventoryOperationRepository;
    private final InventoryRepository inventoryRepository;
    private final AppConfig appConfig;
    private final Logger logger;

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void run(){

        List<InventoryOperation> ops=inventoryOperationRepository.findAllExpiredInventory(InventoryOperationStatus.INVENTORY_OPERATION_STATUS_INITIATED.name(), 
                                InventoryOperationType.INVENTORY_OPERATION_TYPE_CLAIM.name(), 10_000);

        for(InventoryOperation inventoryOperation:ops){
            try {
                reclaimExpiredInventory(inventoryOperation.getInventoryId(), inventoryOperation.getOperationId());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

    }

    // @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void reclaimExpiredInventory(long inventoryId, long operationId){

        // get lock on inventory
        Inventory inventory=inventoryRepository.findById(inventoryId).get();
        if(inventory==null){
            logger.error("inventory not found");
            return;
        }
        InventoryOperation inventoryOperation=inventoryOperationRepository.findById(operationId).get();
        if(inventoryOperation==null){
            logger.error("no such operation with id: {}",operationId);
            return;
        }
        if(inventoryOperation.getInventoryOperationType()!=InventoryOperationType.INVENTORY_OPERATION_TYPE_CLAIM
            || inventoryOperation.getInventoryOperationStatus()!=InventoryOperationStatus.INVENTORY_OPERATION_STATUS_INITIATED){
            logger.error("invalid operation");
            return;
        }
        if(!inventoryOperation.getCreateAt().isBefore(LocalDateTime.now().minusMinutes(appConfig.inventoryClaimExpiryInMinutes))){
            return;
        }

        // claim expired
        inventoryOperation.setInventoryOperationStatus(InventoryOperationStatus.INVENTORY_OPERATION_STATUS_EXPIRED);
        inventory.setQuantity(inventory.getQuantity()+inventoryOperation.getQuantity());

        inventoryOperationRepository.save(inventoryOperation);
        inventoryRepository.save(inventory);

    }
}
