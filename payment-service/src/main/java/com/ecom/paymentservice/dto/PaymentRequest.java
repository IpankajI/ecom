package com.ecom.paymentservice.dto;

import com.ecom.paymentservice.model.PaymentMode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    private PaymentMode paymentMode;
    private String orderId;
}
