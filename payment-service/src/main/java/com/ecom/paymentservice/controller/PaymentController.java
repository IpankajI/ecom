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
import com.ecom.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    
    @GetMapping("health")
    public String health(){
        return "ok";
    }


    @PostMapping("")
    public PaymentResponse createPayment(@RequestBody PaymentRequest paymentRequest){
        PaymentResponse paymentResponse=paymentService.initiatePayment(paymentRequest);
        return paymentResponse;
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable("paymentId") Long paymentId){
        return paymentService.getPayment(paymentId);
    }

    @PatchMapping("/{paymentId}/status")
    public PaymentResponse updatePaymentStatus(@PathVariable("paymentId") Long paymentId, @RequestBody UpdatePaymentStatusRequest updatePaymentStatusRequest){
        PaymentResponse paymentResponse=paymentService.updateStatus(paymentId, updatePaymentStatusRequest.getPaymentStatus());
        return paymentResponse;
    }
}
