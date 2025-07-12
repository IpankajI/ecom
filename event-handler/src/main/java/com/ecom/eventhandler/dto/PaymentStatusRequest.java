package com.ecom.eventhandler.dto;

import com.ecom.eventhandler.event.OrderPaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentStatusRequest {
    OrderPaymentStatus paymentStatus;
}

// {
//     "paymentStatus": "ORDER_PAYMENT_STATUS_COMPLETED"
// }