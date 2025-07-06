package com.ecom.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.paymentservice.Repository.PaymentRepository;
import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentEvent;
import com.ecom.paymentservice.model.PaymentStatus;
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

    public PaymentResponse initiatePayment(PaymentRequest paymentRequest){
        OrderResponse orderResponse=webClient
            .get()
            .uri("http://localhost:30003/api/orders/"+paymentRequest.getOrderId())
            .retrieve()
            .bodyToMono(OrderResponse.class)
            .block();
        
        if(orderResponse==null || orderResponse.getId()==null){
            throw new RuntimeException("no such order");
        }

        LocalDateTime now=LocalDateTime.now();
        Payment payment=Payment.builder()
            .createdAt(now)
            .orderId(paymentRequest.getOrderId())
            .paymentMode(paymentRequest.getPaymentMode())
            .updatedAt(now)
            .paymentStatus(PaymentStatus.PaymentStatusInitiated)
            .build();

        paymentRepository.save(payment);
        PaymentResponse paymentResponse=PaymentResponseFrom(payment);
        sendPaymentEvent(paymentResponse);

        return paymentResponse;
    }

    @Transactional
    public PaymentResponse getPayment(Integer paymentId){
        Payment payment=paymentRepository.findById(paymentId).get();
        return PaymentResponseFrom(payment);
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public PaymentResponse updateStatus(Integer paymentId, PaymentStatus paymentStatus){

        Payment payment=paymentRepository.findById(paymentId).get();

        if(!validateStatusUpdate(payment, paymentStatus)){
            throw new RuntimeException("invalid request for payment update");
        }

        payment.setPaymentStatus(paymentStatus);
        paymentRepository.save(payment);
        PaymentResponse paymentResponse=PaymentResponseFrom(payment);

        sendPaymentEvent(paymentResponse);

        return paymentResponse;
    }

    private void sendPaymentEvent(PaymentResponse paymentResponse){
        String queueUrl="/000000000000/queue-payment-events";
        
        try {
            // Send a message
            PaymentEvent paymentEvent=PaymentEvent.builder()
                .id(paymentResponse.getId())
                .orderId(paymentResponse.getOrderId())
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
            System.out.println("Message sent successfully payment status event!");

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            // System.exit(1);
        } catch(JsonProcessingException e){
            System.err.println(e.getMessage());
        }
        finally {
            // sqsClient.close();
        }
        

    }

    private boolean validateStatusUpdate(Payment payment, PaymentStatus newPaymentStatus){
        switch (payment.getPaymentStatus()) {
            case PaymentStatusFailed:
                if(newPaymentStatus==PaymentStatus.PaymentStatusInitiated){
                    return true;
                }
                break;
            case PaymentStatusInitiated:
                if(newPaymentStatus==PaymentStatus.PaymentStatusSuccess){
                    return true;
                }
                break;
            case PaymentStatusSuccess:
                if(newPaymentStatus==PaymentStatus.PaymentStatusRefundInitiated){
                    return true;
                }
                break;
            case PaymentStatusRefundInitiated:
                if(newPaymentStatus==PaymentStatus.PaymentStatusRefunded){
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
    }

    private PaymentResponse PaymentResponseFrom(Payment payment){
        PaymentResponse paymentResponse=PaymentResponse.builder()
            .createdAt(payment.getCreatedAt())
            .id(payment.getId())
            .orderId(payment.getOrderId())
            .paymentMode(payment.getPaymentMode())
            .paymentStatus(payment.getPaymentStatus())
            .updatedAt(payment.getUpdatedAt())
            .build();
        return paymentResponse;
    }
}

@Data
class OrderResponse {
    private Integer id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String status;
}
