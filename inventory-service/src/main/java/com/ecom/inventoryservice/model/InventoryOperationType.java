package com.ecom.inventoryservice.model;

public enum InventoryOperationType {
    INVENTORY_OPERATION_TYPE_ADD(1000),
    INVENTORY_OPERATION_TYPE_CLAIM(20000);


    private final int value;

    private InventoryOperationType(int value){
        this.value=value;
    }

    public int value(){
        return value;
    }
}



