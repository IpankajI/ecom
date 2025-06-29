package com.ecom.orderservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.orderservice.dto.OrderLineItemRequest;
import com.ecom.orderservice.dto.OrderLineItemResponse;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderLineItem;
import com.ecom.orderservice.model.OrderPaymentStatus;
import com.ecom.orderservice.model.OrderStatus;
import com.ecom.orderservice.repository.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class OrderService {
    

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    
    public OrderResponse createOrder(OrderRequest orderRequest){

        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setTotalAmount(new BigDecimal(0));

        List<OrderLineItem> orderLineItems= orderRequest.getOrderLineItemRequests().stream().map(
            orderLineItemRequest -> orderLineItemFrom(orderLineItemRequest, order) ).toList();
        order.setOrderLineItems(orderLineItems);

        order.setStatus(OrderStatus.OrderStatusCreated);
        order.setPaymentStatus(OrderPaymentStatus.OrderPaymentStatusPending);

        Order orderSaved=orderRepository.save(order);

        return orderResponseFrom(orderSaved);
    }

    @Transactional
    public OrderResponse getOrder(Long orderId){
        Order order=orderRepository.findById(orderId).get();
        return orderResponseFrom(order);
    }

    public List<OrderResponse> getOrders(){
        List<Order> orders=orderRepository.findAll();

        List<OrderResponse> orderResponses=orders.stream().map(this::orderResponseFrom).toList();

        // Boolean data=webClient.get().uri("http://localhost:30002/api/inventories/iphone_12").retrieve().bodyToMono(Boolean.class).block();

        // System.out.println(".............. "+data);

        return orderResponses;
        
    }


    @Transactional
    public OrderResponse updateOrderPaymentStatus(Long orderId, OrderPaymentStatus paymentStatus){
        Order order=orderRepository.findById(orderId).get();
        if(!validateUpdateOrderPaymentStatus(order, paymentStatus)){
            throw new RuntimeException("order payment status update not allowed");
        }
        order.setPaymentStatus(paymentStatus);
        order=orderRepository.save(order);
        return orderResponseFrom(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus orderStatus){
        Order order=orderRepository.findById(orderId).get();
        if(!validateUpdateOrderStatus(order, orderStatus)){
            throw new RuntimeException("order status update not allowed");
        }
        order.setStatus(orderStatus);
        order=orderRepository.save(order);
        return orderResponseFrom(order);
    }

    private boolean validateUpdateOrderStatus(Order order, OrderStatus newOrderStatus){
        switch (order.getStatus()) {
            case OrderStatusCreated:
                if(newOrderStatus==OrderStatus.OrderStatusComplete || newOrderStatus==OrderStatus.OrderStatusCancelled){
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean validateUpdateOrderPaymentStatus(Order order, OrderPaymentStatus newOrderPaymentStatus){
        switch (order.getPaymentStatus()) {
            case OrderPaymentStatusInitiated:
                if(newOrderPaymentStatus==OrderPaymentStatus.OrderPaymentStatusCompleted){
                    return true;
                }
                break;
            case OrderPaymentStatus.OrderPaymentStatusPending:
                if(newOrderPaymentStatus==OrderPaymentStatus.OrderPaymentStatusInitiated){
                    return true;
                }
                break;
            case OrderPaymentStatus.OrderPaymentStatusCompleted:
                if(newOrderPaymentStatus==OrderPaymentStatus.OrderPaymentStatusRefundInitiated){
                    return true;
                }
                break;
            case OrderPaymentStatus.OrderPaymentStatusRefundInitiated:
                if(newOrderPaymentStatus==OrderPaymentStatus.OrderPaymentStatusRefunded){
                    return true;
                }
                break;
        
            default:
                break;
        }
        return false;
    }

    private OrderLineItem orderLineItemFrom(OrderLineItemRequest orderLineItemRequest, Order order){
        
        
        Product product=webClient.get()
            .uri("http://localhost:30001/api/products/"+orderLineItemRequest.getProductId())
            .retrieve()
            .bodyToMono(Product.class)
            .block();


        OrderLineItem orderLineItem=new OrderLineItem();
        
        BigDecimal lineItemTotal=product.getPrice().multiply(BigDecimal.valueOf(orderLineItemRequest.getQuantity()));

        orderLineItem.setTotalAmount(lineItemTotal);
        orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
        orderLineItem.setSkuCode("NA");
       
        orderLineItem.setOrder(order);

        orderLineItem.setProductId(orderLineItemRequest.getProductId());
        
        order.setTotalAmount(lineItemTotal.add(order.getTotalAmount()));

        return orderLineItem;

    }

    private OrderLineItemResponse orderLineItemResponseFrom(OrderLineItem orderLineItem){
        OrderLineItemResponse orderLineItemResponse=new OrderLineItemResponse();

        orderLineItemResponse.setId(orderLineItem.getId());
        orderLineItemResponse.setTotalAmount(orderLineItem.getTotalAmount());
        orderLineItemResponse.setQuantity(orderLineItem.getQuantity());
        orderLineItemResponse.setSkuCode(orderLineItem.getSkuCode());
        orderLineItemResponse.setProductId(orderLineItem.getProductId());

        return orderLineItemResponse;

    }


    private OrderResponse orderResponseFrom(Order order){
        OrderResponse orderResponse=new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setOrderLineItemResponses(
            order.getOrderLineItems().stream().map(orderLineItem -> orderLineItemResponseFrom(orderLineItem)).toList()
        );
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setPaymentStatus(order.getPaymentStatus());
        orderResponse.setStatus(order.getStatus());
        return orderResponse;
    }




}


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Product {

    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    
}
