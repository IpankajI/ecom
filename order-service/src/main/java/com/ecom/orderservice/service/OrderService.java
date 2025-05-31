package com.ecom.orderservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.orderservice.dto.OrderLineItemRequest;
import com.ecom.orderservice.dto.OrderLineItemResponse;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderLineItem;
import com.ecom.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    
    public void createOrder(@RequestBody OrderRequest orderRequest){

        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems= orderRequest.getOrderLineItemRequests().stream().map(
            orderLineItemRequest -> orderLineItemFrom(orderLineItemRequest, order) ).toList();
        order.setOrderLineItems(orderLineItems);

        orderRepository.save(order);
    }

    public OrderResponse getOrder(Long orderId){
        Order order=orderRepository.findById(orderId).orElse(null);

        OrderResponse orderResponse=new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setOrderNumber(order.getOrderNumber());


        orderResponse.setOrderLineItemResponses(
            order.getOrderLineItems().stream().map(orderLineItem -> orderLineItemResponseFrom(orderLineItem)).toList()
        );

        return orderResponse;


    }


    @GetMapping
    public List<OrderResponse> getOrders(){
        List<Order> orders=orderRepository.findAll();

        List<OrderResponse> orderResponses=orders.stream().map(this::orderResponseFrom).toList();

        Boolean data=webClient.get().uri("http://localhost:8080/api/inventories/iphone_12").retrieve().bodyToMono(Boolean.class).block();

        System.out.println(".............. "+data);

        return orderResponses;
        
    }

    private OrderLineItem orderLineItemFrom(OrderLineItemRequest orderLineItemRequest, Order order){
        OrderLineItem orderLineItem=new OrderLineItem();

        orderLineItem.setPrice(orderLineItemRequest.getPrice());
        orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
        orderLineItem.setSkuCode(orderLineItemRequest.getSkuCode());
        orderLineItem.setOrder(order);

        return orderLineItem;

    }

    private OrderLineItemResponse orderLineItemResponseFrom(OrderLineItem orderLineItem){
        OrderLineItemResponse orderLineItemResponse=new OrderLineItemResponse();

        orderLineItemResponse.setId(orderLineItem.getId());
        orderLineItemResponse.setPrice(orderLineItem.getPrice());
        orderLineItemResponse.setQuantity(orderLineItem.getQuantity());
        orderLineItemResponse.setSkuCode(orderLineItem.getSkuCode());

        return orderLineItemResponse;

    }


    private OrderResponse orderResponseFrom(Order order){
        OrderResponse orderResponse=new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setOrderLineItemResponses(
            order.getOrderLineItems().stream().map(orderLineItem -> orderLineItemResponseFrom(orderLineItem)).toList()
        );
        orderResponse.setOrderNumber(order.getOrderNumber());

        return orderResponse;
    }




}
