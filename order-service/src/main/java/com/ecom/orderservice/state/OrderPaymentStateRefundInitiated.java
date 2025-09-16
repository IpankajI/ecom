package com.ecom.orderservice.state;

import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderPaymentStatus;

public class OrderPaymentStateRefundInitiated implements OrderPaymentState{

    @Override
    public boolean update(Order order, OrderPaymentStatus newOrderPaymentStatus){
        if(order.getPaymentStatus().equals(OrderPaymentStatus.ORDER_PAYMENT_STATUS_REFUND_INITIATED) 
            && newOrderPaymentStatus.equals(OrderPaymentStatus.ORDER_PAYMENT_STATUS_REFUNDED)){
            order.setPaymentStatus(newOrderPaymentStatus);
            return true;
        }
        return false;
    }
}