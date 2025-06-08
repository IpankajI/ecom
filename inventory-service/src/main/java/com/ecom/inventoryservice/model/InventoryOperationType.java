package com.ecom.inventoryservice.model;

public enum InventoryOperationType {
    InventoryOperationTypeAdd(1000),
    InventoryOperationTypeClaim(20000);


    private final int value;

    private InventoryOperationType(int value){
        this.value=value;
    }

    public int value(){
        return value;
    }
}
