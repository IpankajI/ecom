package com.ecom.paymentservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.dto.UpdatePaymentStatusRequest;
import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentMode;
import com.ecom.paymentservice.model.PaymentStatus;
import com.ecom.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
class PaymentControllerTests {
    
    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    MockMvc mockMvc;

    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private Payment expectedPayment;

    @Captor
    ArgumentCaptor<Payment> paymentCaptor;
    @Captor
    ArgumentCaptor<Payment> eventCaptor;

    @BeforeEach
    void setUp(){
        paymentRequest = PaymentRequest.builder()
            .orderId("1000")
            .paymentMode(PaymentMode.PAYMENT_MODE_CASH)
            .build();


        expectedPayment = Payment.builder()
            .id(456L)
            .orderId(1000L)
            .paymentMode(PaymentMode.PAYMENT_MODE_CASH)
            .paymentStatus(PaymentStatus.PAYMENT_STATUS_INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        // paymentResponse=Payment.builder()
        //     .id(idGenerator.next())
        //     .createdAt(now)
        //     .orderId(Long.valueOf(paymentRequest.getOrderId()))
        //     .paymentMode(paymentRequest.getPaymentMode())
        //     .updatedAt(now)
        //     .paymentStatus(PaymentStatus.PAYMENT_STATUS_INITIATED)
        //     .build();
    }

    @Test
    void testHealth() throws Exception{
        mockMvc.perform(get("/api/payments/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("ok"));
    }

    @Test
    void testCreatePayment() throws Exception{
        
        when(paymentService.initiatePayment(any(PaymentRequest.class))).thenReturn(expectedPayment);

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(paymentRequest))
            )
            .andExpect(jsonPath("$.id").value("456"))
            .andExpect(jsonPath("$.orderId").value("1000"))
            .andExpect(jsonPath("$.paymentMode").value("PAYMENT_MODE_CASH"))
            .andExpect(jsonPath("$.paymentStatus").value("PAYMENT_STATUS_INITIATED"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetPayment() throws Exception{
        
        when(paymentService.getPayment(expectedPayment.getId())).thenReturn(expectedPayment);

        mockMvc.perform(get("/api/payments/{paymentId}", expectedPayment.getId()))
            .andExpect(jsonPath("$.id").value("456"))
            .andExpect(jsonPath("$.orderId").value("1000"))
            .andExpect(jsonPath("$.paymentMode").value("PAYMENT_MODE_CASH"))
            .andExpect(jsonPath("$.paymentStatus").value("PAYMENT_STATUS_INITIATED"))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdatePaymentStatus() throws Exception{
        
        expectedPayment.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_SUCCESS);
        when(paymentService.updateStatus(expectedPayment.getId(), PaymentStatus.PAYMENT_STATUS_SUCCESS)).thenReturn(expectedPayment);

        UpdatePaymentStatusRequest paymentStatusRequest=UpdatePaymentStatusRequest.builder()
            .paymentStatus(PaymentStatus.PAYMENT_STATUS_SUCCESS)
            .build();

        mockMvc.perform(patch("/api/payments/{paymentId}/status", expectedPayment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(paymentStatusRequest))
            )
            .andExpect(jsonPath("$.id").value("456"))
            .andExpect(jsonPath("$.orderId").value("1000"))
            .andExpect(jsonPath("$.paymentMode").value("PAYMENT_MODE_CASH"))
            .andExpect(jsonPath("$.paymentStatus").value("PAYMENT_STATUS_SUCCESS"))
            .andExpect(status().isOk());
    }
}