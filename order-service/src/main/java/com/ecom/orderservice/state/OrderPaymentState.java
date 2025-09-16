package com.ecom.orderservice.state;

import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderPaymentStatus;

public interface OrderPaymentState {
    boolean update(Order order, OrderPaymentStatus newOrderPaymentStatus);
}
