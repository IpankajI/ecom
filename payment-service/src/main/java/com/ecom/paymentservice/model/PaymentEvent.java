package com.ecom.paymentservice.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentEvent {
    private Long id;
    private Long orderId;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
