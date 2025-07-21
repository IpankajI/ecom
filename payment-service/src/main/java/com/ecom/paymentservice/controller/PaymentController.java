package com.ecom.paymentservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.dto.UpdatePaymentStatusRequest;
import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.service.PaymentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "payment apis")
public class PaymentController {

    private final PaymentService paymentService;
    
    @GetMapping("health")
    public String health(){
        return "ok";
    }

    @PostMapping("")
    public PaymentResponse createPayment(@RequestBody PaymentRequest paymentRequest){
        return paymentResponseFrom(paymentService.initiatePayment(paymentRequest));
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable("paymentId") Long paymentId){
        return paymentResponseFrom(paymentService.getPayment(paymentId));
    }

    @PatchMapping("/{paymentId}/status")
    public PaymentResponse updatePaymentStatus(@PathVariable("paymentId") Long paymentId, @RequestBody UpdatePaymentStatusRequest updatePaymentStatusRequest){
        return paymentResponseFrom(paymentService.updateStatus(paymentId, updatePaymentStatusRequest.getPaymentStatus()));
    }

    private PaymentResponse paymentResponseFrom(Payment payment){
        return PaymentResponse.builder()
            .createdAt(payment.getCreatedAt())
            .id(payment.getId().toString())
            .orderId(payment.getOrderId().toString())
            .paymentMode(payment.getPaymentMode())
            .paymentStatus(payment.getPaymentStatus())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }
}
