package com.ecom.inventoryservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.inventoryservice.appconfig.AppConfig;
import com.ecom.inventoryservice.model.Inventory;
import com.ecom.inventoryservice.model.InventoryOperation;
import com.ecom.inventoryservice.model.InventoryOperationStatus;
import com.ecom.inventoryservice.model.InventoryOperationType;
import com.ecom.inventoryservice.repository.InventoryOperationRepository;
import com.ecom.inventoryservice.repository.InventoryRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryOperationRepository inventoryOperationRepository;
    private final WebClient webClient;
    private final AppConfig appConfig;

    public boolean isInStock(String skuCode){

        Optional<Inventory> inventory=inventoryRepository.findBySkuCode(skuCode);
        if (!inventory.isPresent() ){
            return false;
        }

        return inventory.isPresent() && inventory.get().getQuantity()>0;
    }

    public Inventory getInventory(String productId){

        Inventory inventory=inventoryRepository.getByProductId(productId);

        return inventory;
    }

    public List<Inventory> getInventories(){
        List<Inventory> inventories=inventoryRepository.findAll();
        return inventories;
    }


    public void addInventory(Inventory inventory){

        Product product=webClient
            .get()
            .uri("http://localhost:30001/api/products/"+inventory.getProductId())
            .retrieve()
            .bodyToMono(Product.class)
            .block();

        if(product==null || !product.getId().equals(inventory.getProductId())){
            throw new RuntimeException("no such product available");
        }

        inventoryRepository.save(inventory);
    }

    @Transactional
    public void incrementQuantityBy(String productId , Integer incBy){
        Inventory inventory=inventoryRepository.getByProductId(productId);
        if(inventory==null){
            throw new RuntimeException("no such product");
        }

        inventory.setQuantity(inventory.getQuantity()+incBy);
        inventoryRepository.save(inventory);

        LocalDateTime now=LocalDateTime.now();

        InventoryOperation inventoryOperation=InventoryOperation.builder()
                        .createAt(now)
                        .inventoryId(inventory.getId())
                        .inventoryOperationStatus(InventoryOperationStatus.InventoryOperationStatusCompleted)
                        .inventoryOperationType(InventoryOperationType.InventoryOperationTypeAdd)
                        .operationId(UUID.randomUUID().toString())
                        .quantity(incBy)
                        .updatedAt(now)
                        .build();
        inventoryOperationRepository.save(inventoryOperation);
    }

    @Transactional
    public void claimInventory(String productId, Integer quanity){
        if(quanity<=0){
            throw new RuntimeException("claim quanity should be positive");
        }
        Inventory inventory=inventoryRepository.getByProductId(productId);
        if(inventory==null){
            throw new RuntimeException("no such product");
        }

        if(inventory.getQuantity()<quanity){
            throw new RuntimeException("not enough stock");
        }

        inventory.setQuantity(inventory.getQuantity()-quanity);
        inventoryRepository.save(inventory);

        LocalDateTime now=LocalDateTime.now();

        InventoryOperation inventoryOperation=InventoryOperation.builder()
                        .createAt(now)
                        .inventoryId(inventory.getId())
                        .inventoryOperationStatus(InventoryOperationStatus.InventoryOperationStatusInitiated)
                        .inventoryOperationType(InventoryOperationType.InventoryOperationTypeClaim)
                        .operationId(UUID.randomUUID().toString())
                        .quantity(quanity)
                        .updatedAt(now)
                        .build();
        inventoryOperationRepository.save(inventoryOperation);
    }

    @Transactional
    public void markInventoryClaimSold(String operationId){
        InventoryOperation inventoryOperation=inventoryOperationRepository.findById(operationId).get();
        if(inventoryOperation==null){
            throw new RuntimeException("no such claim");
        }

        if(inventoryOperation.getInventoryOperationType()!=InventoryOperationType.InventoryOperationTypeClaim
            || inventoryOperation.getInventoryOperationStatus()!=InventoryOperationStatus.InventoryOperationStatusInitiated){
            throw new RuntimeException("invalid operation");
        }

        if(inventoryOperation.getCreateAt().isBefore(LocalDateTime.now().minusMinutes(appConfig.inventoryClaimExpiryInMinutes))){
            throw new RuntimeException("claim expired");
        }

        inventoryOperation.setInventoryOperationStatus(InventoryOperationStatus.InventoryOperationStatusCompleted);   
        
        inventoryOperationRepository.save(inventoryOperation);

    }
}


@Getter
class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    
}