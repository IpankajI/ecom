package com.ecom.inventoryservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.inventoryservice.dto.ClaimInventoryRequest;
import com.ecom.inventoryservice.dto.ClaimInventoryResponse;
import com.ecom.inventoryservice.dto.IncrementInventoryQuantityRequest;
import com.ecom.inventoryservice.dto.InventoryRequest;
import com.ecom.inventoryservice.dto.InventoryResponse;
import com.ecom.inventoryservice.model.Inventory;
import com.ecom.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {
    
    private final InventoryService inventoryService;


    @GetMapping
    public List<InventoryResponse> getInventories(){
        List<Inventory> inventories=inventoryService.getInventories();
        if(inventories==null){
            throw new RuntimeException("no inventory");
        }
        List<InventoryResponse> inventoryResponses = inventories.stream().map(inventory -> InventoryResponseFrom(inventory)).toList();
        return inventoryResponses;
    }

    @PostMapping
    public void addInventory(@RequestBody InventoryRequest inventoryRequest){
        Inventory inventory=Inventory.builder()
                    .productId(inventoryRequest.getProductId())
                    .quantity(0)
                    .skuCode(inventoryRequest.getSkuCode())
                    .build();
        inventoryService.addInventory(inventory);
    }

    @GetMapping("/{productId}")
    public InventoryResponse getInventory(@PathVariable("productId") String productId){


        Inventory inventory=inventoryService.getInventory(productId);

        if(inventory==null){
            throw new RuntimeException("no such inventory");
        }

        return InventoryResponseFrom(inventory);
    }


    private InventoryResponse InventoryResponseFrom(Inventory inventory){

        return InventoryResponse.builder()
            .skuCode(inventory.getSkuCode())
            .productId(inventory.getProductId())
            .quantity(inventory.getQuantity())
            .build();
    }

    @PatchMapping("/{productId}")
    public void incrementInventoryQuantity(@PathVariable("productId") String productId, @RequestBody IncrementInventoryQuantityRequest incrementInventoryQuantityRequest ){
        inventoryService.incrementQuantityBy(productId, incrementInventoryQuantityRequest.getIncBy());
    }

    @PostMapping("/{productId}/claim")
    public ClaimInventoryResponse claimInventoryQuantity(@PathVariable("productId") String productId, @RequestBody ClaimInventoryRequest claimInventoryRequest ){
        return inventoryService.claimInventory(productId, claimInventoryRequest.getQuantity());
    }

    @PatchMapping("/{productId}/claim/{claim_id}")
    public void markClaimCompleted(@PathVariable("claim_id") String claimId){
        inventoryService.markInventoryClaimSold(claimId);
    }

}
