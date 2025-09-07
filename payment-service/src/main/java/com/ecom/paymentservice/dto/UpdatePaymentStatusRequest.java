package com.ecom.paymentservice.dto;

import com.ecom.paymentservice.model.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePaymentStatusRequest {
    private PaymentStatus paymentStatus;
}
