package com.ecom.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.paymentservice.repository.PaymentRepository;
import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentEvent;
import com.ecom.paymentservice.model.PaymentStatus;
import com.ecom.paymentservice.utils.IDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.transaction.annotation.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final WebClient webClient;
    private final SqsClient sqsClient;
    private final IDGenerator idGenerator;
    private final Logger logger;

    public PaymentResponse initiatePayment(PaymentRequest paymentRequest){
        OrderResponse orderResponse=webClient
            .get()
            .uri("http://order-service:30003/api/orders/"+paymentRequest.getOrderId())
            .retrieve()
            .bodyToMono(OrderResponse.class)
            .block();
        
        if(orderResponse==null || orderResponse.getId()==null){
            logger.error("no such order");
            return null;
        }

        LocalDateTime now=LocalDateTime.now();
        Payment payment=Payment.builder()
            .id(idGenerator.next())
            .createdAt(now)
            .orderId(Long.valueOf(paymentRequest.getOrderId()))
            .paymentMode(paymentRequest.getPaymentMode())
            .updatedAt(now)
            .paymentStatus(PaymentStatus.PAYMENT_STATUS_INITIATED)
            .build();

        paymentRepository.save(payment);
        PaymentResponse paymentResponse=paymentResponseFrom(payment);
        sendPaymentEvent(paymentResponse);
        return paymentResponse;
    }

    @Transactional
    public PaymentResponse getPayment(Long paymentId){
        Optional<Payment> payment=paymentRepository.findById(paymentId);
        if(!payment.isPresent()){
            return null;
        }
        return paymentResponseFrom(payment.get());
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public PaymentResponse updateStatus(Long paymentId, PaymentStatus paymentStatus){

        Payment payment=paymentRepository.findById(paymentId).get();

        if(!validateStatusUpdate(payment, paymentStatus)){
            logger.error("invalid request for payment update");
            return null;
        }

        payment.setPaymentStatus(paymentStatus);
        paymentRepository.save(payment);
        PaymentResponse paymentResponse=paymentResponseFrom(payment);

        sendPaymentEvent(paymentResponse);

        return paymentResponse;
    }

    private void sendPaymentEvent(PaymentResponse paymentResponse){
        String queueUrl="/000000000000/queue-payment-events";
        
        try {
            PaymentEvent paymentEvent=PaymentEvent.builder()
                .id(Long.valueOf(paymentResponse.getId()))
                .orderId(Long.valueOf(paymentResponse.getOrderId()))
                .paymentMode(paymentResponse.getPaymentMode())
                .paymentStatus(paymentResponse.getPaymentStatus())
                .createdAt(paymentResponse.getCreatedAt())
                .updatedAt(paymentResponse.getUpdatedAt())
                .build();
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String msg=objectMapper.writeValueAsString(paymentEvent);
            SendMessageRequest sendRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(msg)
                    .build();
            sqsClient.sendMessage(sendRequest);
        } catch (SqsException|JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        

    }

    private boolean validateStatusUpdate(Payment payment, PaymentStatus newPaymentStatus){
        switch (payment.getPaymentStatus()) {
            case PAYMENT_STATUS_FAILED:
                if(newPaymentStatus==PaymentStatus.PAYMENT_STATUS_INITIATED){
                    return true;
                }
                break;
            case PAYMENT_STATUS_INITIATED:
                if(newPaymentStatus==PaymentStatus.PAYMENT_STATUS_SUCCESS){
                    return true;
                }
                break;
            case PAYMENT_STATUS_SUCCESS:
                if(newPaymentStatus==PaymentStatus.PAYMENT_STATUS_REFUND_INITIATED){
                    return true;
                }
                break;
            case PAYMENT_STATUS_REFUND_INITIATED:
                if(newPaymentStatus==PaymentStatus.PAYMENT_STATUS_REFUNDED){
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
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

@Data
class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String status;
}
