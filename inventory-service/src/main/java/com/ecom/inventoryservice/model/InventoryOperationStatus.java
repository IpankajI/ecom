package com.ecom.inventoryservice.model;

public enum InventoryOperationStatus {
    INVENTORY_OPERATION_STATUS_INITIATED(1000),
    INVENTORY_OPERATION_STATUS_COMPLETED(2000),
    INVENTORY_OPERATION_STATUS_EXPIRED(3000);

    private final int value;

    private InventoryOperationStatus(int value){
        this.value=value;
    }

    public int value(){
        return value;
    }
}



