package com.ecom.paymentservice.dto;

import com.ecom.paymentservice.model.PaymentStatus;

import lombok.Data;

@Data
public class UpdatePaymentStatusRequest {
    private PaymentStatus paymentStatus;
}
