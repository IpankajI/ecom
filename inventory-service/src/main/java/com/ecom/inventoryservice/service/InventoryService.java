package com.ecom.inventoryservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.inventoryservice.appconfig.AppConfig;
import com.ecom.inventoryservice.dto.ClaimInventoryResponse;
import com.ecom.inventoryservice.dto.ProductResponse;
import com.ecom.inventoryservice.model.Inventory;
import com.ecom.inventoryservice.model.InventoryOperation;
import com.ecom.inventoryservice.model.InventoryOperationStatus;
import com.ecom.inventoryservice.model.InventoryOperationType;
import com.ecom.inventoryservice.repository.InventoryOperationRepository;
import com.ecom.inventoryservice.repository.InventoryRepository;
import com.ecom.inventoryservice.utils.IDGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryOperationRepository inventoryOperationRepository;
    private final WebClient webClient;
    private final AppConfig appConfig;
    private final IDGenerator idGenerator;
    private final Logger logger;

    public boolean isInStock(String skuCode){

        Optional<Inventory> inventory=inventoryRepository.findBySkuCode(skuCode);
        if (!inventory.isPresent() ){
            return false;
        }

        return inventory.isPresent() && inventory.get().getQuantity()>0;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Inventory getInventory(Long id){
        Optional<Inventory> inventory=inventoryRepository.findById(id);
        if(inventory.isPresent()){
            return inventory.get();
        }
        return null;
    }

    public List<Inventory> getInventories(){
        return inventoryRepository.findAll();
    }


    public Inventory addInventory(Inventory inventory){

        ProductResponse product=webClient
            .get()
            .uri("http://product-service:30001/api/products/"+inventory.getProductId())
            .retrieve()
            .bodyToMono(ProductResponse.class)
            .block();

        if(product==null || !product.getId().equals(inventory.getProductId())){
            logger.error("no such product available");
            return null;
        }
        inventory.setId(idGenerator.next());
        inventory.setStoreId(0l);

        return inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void incrementQuantityBy(Long id , Integer incBy){
        // also take lock on inventory record

        Optional<Inventory> inventoryRecord=inventoryRepository.findById(id);
        if(inventoryRecord.isEmpty()){
            logger.error("no such product");
            return;
        }
        Inventory inventory=inventoryRecord.get();
        inventory.setQuantity(inventory.getQuantity()+incBy);
        inventoryRepository.save(inventory);

        LocalDateTime now=LocalDateTime.now();

        InventoryOperation inventoryOperation=InventoryOperation.builder()
                        .operationId(idGenerator.next())
                        .createAt(now)
                        .inventoryId(inventory.getId())
                        .inventoryOperationStatus(InventoryOperationStatus.INVENTORY_OPERATION_STATUS_COMPLETED)
                        .inventoryOperationType(InventoryOperationType.INVENTORY_OPERATION_TYPE_ADD)
                        .operationId(idGenerator.next())
                        .quantity(incBy)
                        .updatedAt(now)
                        .build();
        inventoryOperationRepository.save(inventoryOperation);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ClaimInventoryResponse claimInventory(Long id, Integer quanity){
        if(quanity<=0){
            logger.error("claim quanity should be positive");
            return null;
        }
        // also take lock on inventory record
        Optional<Inventory> inventoryRecord=inventoryRepository.findById(id);
        if(!inventoryRecord.isPresent()){
            logger.error("no such product");
            return null;
        }
        Inventory inventory=inventoryRecord.get();
        if(inventory.getQuantity()<quanity){
            logger.error("not enough stock");
            return null;
        }

        inventory.setQuantity(inventory.getQuantity()-quanity);
        inventoryRepository.save(inventory);

        LocalDateTime now=LocalDateTime.now();

        InventoryOperation inventoryOperation=InventoryOperation.builder()
                        .createAt(now)
                        .inventoryId(inventory.getId())
                        .inventoryOperationStatus(InventoryOperationStatus.INVENTORY_OPERATION_STATUS_INITIATED)
                        .inventoryOperationType(InventoryOperationType.INVENTORY_OPERATION_TYPE_CLAIM)
                        .operationId(idGenerator.next())
                        .quantity(quanity)
                        .updatedAt(now)
                        .build();
        inventoryOperation=inventoryOperationRepository.save(inventoryOperation);

        return claimInventoryResponseFrom(inventoryOperation);
    }

    private ClaimInventoryResponse claimInventoryResponseFrom(InventoryOperation inventoryOperation){
        return new ClaimInventoryResponse(inventoryOperation.getOperationId());
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void markInventoryClaimSold(long operationId){
        InventoryOperation inventoryOperation=inventoryOperationRepository.findById(operationId).get();
        if(inventoryOperation==null){
            logger.error("no such claim");
            return;
        }
        if(inventoryOperation.getInventoryOperationStatus()==InventoryOperationStatus.INVENTORY_OPERATION_STATUS_COMPLETED){
            return;
        }
        // also take lock on inventory record
        Inventory inventory=inventoryRepository.findById((long)inventoryOperation.getInventoryId()).get();
        if(inventory==null){
            logger.error("inventory not found");
            return;
        }
        //fetch again to respect the lock
        inventoryOperation=inventoryOperationRepository.findById(operationId).get();
        if(inventoryOperation==null){
            logger.error("no such claim");
            return;
        }

        if(inventoryOperation.getInventoryOperationType()!=InventoryOperationType.INVENTORY_OPERATION_TYPE_CLAIM
            || inventoryOperation.getInventoryOperationStatus()!=InventoryOperationStatus.INVENTORY_OPERATION_STATUS_INITIATED){
            logger.error("invalid operation for op id: {}", operationId);
            return;
        }

        if(inventoryOperation.getCreateAt().isBefore(LocalDateTime.now().minusMinutes(appConfig.inventoryClaimExpiryInMinutes))){
            logger.error("claim expired for op id: {}",operationId);
            return;
        }

        inventoryOperation.setInventoryOperationStatus(InventoryOperationStatus.INVENTORY_OPERATION_STATUS_COMPLETED);   
        
        inventoryOperationRepository.save(inventoryOperation);

    }
}


