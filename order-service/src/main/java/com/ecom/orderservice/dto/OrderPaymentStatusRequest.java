package com.ecom.orderservice.dto;

import com.ecom.orderservice.model.OrderPaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentStatusRequest {
    private OrderPaymentStatus paymentStatus;
}
