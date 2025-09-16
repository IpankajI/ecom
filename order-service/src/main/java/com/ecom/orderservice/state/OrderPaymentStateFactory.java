package com.ecom.orderservice.state;

import com.ecom.orderservice.model.Order;

public class OrderPaymentStateFactory {
    private OrderPaymentStateFactory(){}
    public static OrderPaymentState getState(Order order) {
        switch (order.getPaymentStatus()) {
            case ORDER_PAYMENT_STATUS_INITIATED:
                return new OrderPaymentStateInitiated();
            case ORDER_PAYMENT_STATUS_PENDING:
                return new OrderPaymentStatePending();
            case ORDER_PAYMENT_STATUS_COMPLETED:
                return new OrderPaymentStateCompleted();
            case ORDER_PAYMENT_STATUS_REFUND_INITIATED:
                return new OrderPaymentStateRefundInitiated();
            case ORDER_PAYMENT_STATUS_FAILED:
                return new OrderPaymentStateFailed();
            case ORDER_PAYMENT_STATUS_REFUNDED:
                return new OrderPaymentStateRefunded();
            default:
                throw new IllegalArgumentException("Invalid state provided: " + order.getPaymentStatus());
        }
    }
}
