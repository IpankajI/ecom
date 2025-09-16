package com.ecom.orderservice.state;

import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderPaymentStatus;

public class OrderPaymentStateFailed implements OrderPaymentState{

    @Override
    public boolean update(Order order, OrderPaymentStatus newOrderPaymentStatus){
        return false;
    }
}
