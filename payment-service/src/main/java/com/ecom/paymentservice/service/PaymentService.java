package com.ecom.paymentservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.paymentservice.client.OrderServiceClient;
import com.ecom.paymentservice.dto.OrderResponse;
import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentStatus;
import com.ecom.paymentservice.publisher.PaymentEventPublisher;
import com.ecom.paymentservice.repository.PaymentRepository;
import com.ecom.paymentservice.utils.IDGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final IDGenerator idGenerator;
    private final OrderServiceClient orderServiceClient;
    private final PaymentEventPublisher paymentEventPublisher;

    public Payment initiatePayment(PaymentRequest paymentRequest){
        OrderResponse orderResponse=orderServiceClient.getOrderById(paymentRequest.getOrderId());
        
        if(orderResponse==null || orderResponse.getId()==null){
            log.error("no such order with id: {}", paymentRequest.getOrderId());
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

        payment=paymentRepository.save(payment);
        paymentEventPublisher.sendPaymentEvent(payment);
        return payment;
    }

    @Transactional
    public Payment getPayment(Long paymentId){
        Optional<Payment> payment=paymentRepository.findById(paymentId);
        if(!payment.isPresent()){
            return null;
        }
        return payment.get();
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Payment updateStatus(Long paymentId, PaymentStatus paymentStatus){
        Optional<Payment> paymentData=paymentRepository.findById(paymentId);
        if(!paymentData.isPresent()){
            return null;
        }
        Payment payment=paymentData.get();
        if(!validateStatusUpdate(payment, paymentStatus)){
            log.error("invalid request for payment update: "+paymentId);
            throw new RuntimeException("cannot update payment");
        }
        payment.setPaymentStatus(paymentStatus);
        payment=paymentRepository.save(payment);
        paymentEventPublisher.sendPaymentEvent(payment);
        return payment;
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
}
