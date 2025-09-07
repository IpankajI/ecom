package com.ecom.paymentservice.publisher;

import org.springframework.stereotype.Component;

import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {
    
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    
    public void sendPaymentEvent(Payment payment) {
        String queueUrl = "/000000000000/queue-payment-events";
        
        try {
            PaymentEvent paymentEvent = PaymentEvent.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .paymentMode(payment.getPaymentMode())
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
            
            String msg = objectMapper.writeValueAsString(paymentEvent);
            SendMessageRequest sendRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(msg)
                .build();
            sqsClient.sendMessage(sendRequest);
        } catch (SqsException | JsonProcessingException e) {
            log.error("Failed to send payment event: {}", e.getMessage());
        }
    }
}
