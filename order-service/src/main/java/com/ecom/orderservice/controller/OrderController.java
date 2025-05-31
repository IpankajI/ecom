package com.ecom.orderservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequest orderRequest){
        orderService.createOrder(orderRequest);

        return "created";
    }


    @GetMapping("/{order_id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable("order_id") Long orderId){
        OrderResponse orderResponse=orderService.getOrder(orderId);
        return orderResponse;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrders(){
        List<OrderResponse> orderResponse=orderService.getOrders();
        return orderResponse;
    }
}
