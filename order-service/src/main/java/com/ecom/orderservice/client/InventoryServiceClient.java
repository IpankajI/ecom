package com.ecom.orderservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.orderservice.dto.Inventory;
import com.ecom.orderservice.dto.InventoryClaim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Component
public class InventoryServiceClient {
    private final WebClient webClient;
    private static final String INVENTORY_SERVICE_ENDPOINT="http://product-service:30001/api/products/";


    public Inventory getInventoryById(String id){
        return  webClient.get()
            .uri(INVENTORY_SERVICE_ENDPOINT+id)
            .retrieve()
            .bodyToMono(Inventory.class)
            .block();
    }

    public InventoryClaim claimInventory(Long invetoryId, int quantity){
        InventoryClaimRequest inventoryClaimRequest=InventoryClaimRequest.builder().quantity(quantity).build();

        return webClient.post()
            .uri(INVENTORY_SERVICE_ENDPOINT+invetoryId+"/claim")
            .bodyValue(inventoryClaimRequest)
            .retrieve()
            .bodyToMono(InventoryClaim.class)
            .block();
    }

    public void markClaimedInventory(Long inventoryClaimId){
        webClient.patch()
            .uri(INVENTORY_SERVICE_ENDPOINT+"/claim/"+inventoryClaimId+"/mark")
            .retrieve()
            .bodyToMono(Void.class)
            .block(); 
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder 
class InventoryClaimRequest {
    private Integer quantity;
}
