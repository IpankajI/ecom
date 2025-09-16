package com.ecom.orderservice.state;

import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderPaymentStatus;

public class OrderPaymentStateCompleted implements OrderPaymentState{

    @Override
    public boolean update(Order order, OrderPaymentStatus newOrderPaymentStatus){
        if(order.getPaymentStatus().equals(OrderPaymentStatus.ORDER_PAYMENT_STATUS_COMPLETED) 
            && newOrderPaymentStatus.equals(OrderPaymentStatus.ORDER_PAYMENT_STATUS_REFUND_INITIATED)){
            order.setPaymentStatus(newOrderPaymentStatus);
            return true;
        }
        
        return false;

    }
}