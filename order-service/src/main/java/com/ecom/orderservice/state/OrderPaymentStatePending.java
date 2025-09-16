package com.ecom.orderservice.state;

import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderPaymentStatus;

public class OrderPaymentStatePending implements OrderPaymentState{

    @Override
    public boolean update(Order order, OrderPaymentStatus newOrderPaymentStatus){
        if(order.getPaymentStatus().equals(OrderPaymentStatus.ORDER_PAYMENT_STATUS_PENDING) 
            && newOrderPaymentStatus.equals(OrderPaymentStatus.ORDER_PAYMENT_STATUS_INITIATED)){
            order.setPaymentStatus(newOrderPaymentStatus);
            return true;
        }
        return false;
    }
}