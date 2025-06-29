package com.ecom.orderservice.model;

public enum OrderPaymentStatus {
    OrderPaymentStatusPending,
    OrderPaymentStatusInitiated,
    OrderPaymentStatusSuccess,
    OrderPaymentStatusFailed,
    OrderPaymentStatusRefundInitiated,
    OrderPaymentStatusRefunded
}
