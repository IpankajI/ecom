package com.ecom.inventoryservice.model;

public enum InventoryOperationStatus {
    InventoryOperationStatusInitiated(1000),
    InventoryOperationStatusCompleted(2000),
    InventoryOperationStatusExpired(3000);

    private final int value;

    private InventoryOperationStatus(int value){
        this.value=value;
    }

    public int value(){
        return value;
    }
}
