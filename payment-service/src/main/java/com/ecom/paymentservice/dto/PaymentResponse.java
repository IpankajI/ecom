package com.ecom.paymentservice.dto;

import java.time.LocalDateTime;

import com.ecom.paymentservice.model.PaymentMode;
import com.ecom.paymentservice.model.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String id;
    private String orderId;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
