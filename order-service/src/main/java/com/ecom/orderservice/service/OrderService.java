package com.ecom.orderservice.service;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.proxy.ProxyConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.orderservice.dto.InventoryClaimRequest;
import com.ecom.orderservice.dto.InventoryClaimResponse;
import com.ecom.orderservice.dto.InventoryResponse;
import com.ecom.orderservice.dto.OrderLineItemRequest;
import com.ecom.orderservice.dto.OrderLineItemResponse;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderLineItem;
import com.ecom.orderservice.model.OrderPaymentStatus;
import com.ecom.orderservice.model.OrderStatus;
import com.ecom.orderservice.repository.OrderRepository;
import com.ecom.orderservice.utils.IDGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;
import software.amazon.awssdk.services.sqs.endpoints.SqsEndpointParams;
import software.amazon.awssdk.services.sqs.endpoints.SqsEndpointProvider;
import software.amazon.awssdk.services.sqs.endpoints.internal.AwsEndpointProviderUtils;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
import software.amazon.awssdk.services.sqs.model.SqsRequest;

@Service
@RequiredArgsConstructor
public class OrderService {
    

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    // private final SqsClient sqsClient;
    private final SqsAsyncClient sqsClient;
    private final IDGenerator idGenerator;

    public OrderResponse createOrder(OrderRequest orderRequest){

        Order order=new Order();
        order.setId(idGenerator.next());
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setTotalAmount(new BigDecimal(0));

        List<OrderLineItem> orderLineItems= orderRequest.getOrderLineItemRequests().stream().map(
            orderLineItemRequest -> orderLineItemFrom(orderLineItemRequest, order) ).toList();
        order.setOrderLineItems(orderLineItems);

        order.setStatus(OrderStatus.OrderStatusCreated);
        order.setPaymentStatus(OrderPaymentStatus.OrderPaymentStatusPending);
        claimInventory(orderLineItems);
        Order orderSaved=orderRepository.save(order);

        return orderResponseFrom(orderSaved);
    }

    private void claimInventory(List<OrderLineItem> orderLineItems){

        for(OrderLineItem orderLineItem: orderLineItems){
            InventoryClaimRequest inventoryClaimRequest=InventoryClaimRequest.builder().quantity(orderLineItem.getQuantity()).build();
            InventoryClaimResponse inventoryClaimResponse=webClient.post()
            .uri("http://inventory-service:30002/api/inventories/"+orderLineItem.getInventoryId()+"/claim")
            .bodyValue(inventoryClaimRequest)
            .retrieve()
            .bodyToMono(InventoryClaimResponse.class)
            .block();

            orderLineItem.setInventoryClaimId(inventoryClaimResponse.getClaimId());
        }

    }

    private void markClaimedInventory(List<OrderLineItem> orderLineItems){

        for(OrderLineItem orderLineItem: orderLineItems){
            webClient.patch()
                .uri("http://inventory-service:30002/api/inventories/"+orderLineItem.getInventoryId()+"/claim/"+orderLineItem.getInventoryClaimId())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        }

    }

    @Transactional
    public OrderResponse getOrder(Long orderId){
        Order order=orderRepository.findById(orderId).get();
        return orderResponseFrom(order);
    }



    public List<OrderResponse> getOrders(){
        List<Order> orders=orderRepository.findAll();

        List<OrderResponse> orderResponses=orders.stream().map(this::orderResponseFrom).toList();

        return orderResponses;
        
    }


    @Transactional
    public OrderResponse updateOrderPaymentStatus(Long orderId, OrderPaymentStatus paymentStatus){
        Order order=orderRepository.findById(orderId).get();
        if(order.getPaymentStatus()==paymentStatus){
            return orderResponseFrom(order);
        }
        System.out.println(".....order "+orderId+" "+paymentStatus+" "+order.getPaymentStatus());

        markClaimedInventory(order.getOrderLineItems());

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
        System.out.println("......... "+order.getPaymentStatus()+" "+newOrderPaymentStatus);
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
        
        InventoryResponse inventoryResponse= webClient.get()
                .uri("http://inventory-service:30002/api/inventories/"+orderLineItemRequest.getInventoryId())
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();
        
        Product product=webClient.get()
            .uri("http://product-service:30001/api/products/"+inventoryResponse.getProductId())
            .retrieve()
            .bodyToMono(Product.class)
            .block();


        OrderLineItem orderLineItem=new OrderLineItem();
        
        BigDecimal lineItemTotal=product.getPrice().multiply(BigDecimal.valueOf(orderLineItemRequest.getQuantity()));

        orderLineItem.setId(idGenerator.next());
        orderLineItem.setTotalAmount(lineItemTotal);
        orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
        orderLineItem.setSkuCode("NA");
       
        orderLineItem.setOrder(order);

        orderLineItem.setInventoryId(orderLineItemRequest.getInventoryId());
        
        order.setTotalAmount(lineItemTotal.add(order.getTotalAmount()));

        return orderLineItem;

    }

    private OrderLineItemResponse orderLineItemResponseFrom(OrderLineItem orderLineItem){
        OrderLineItemResponse orderLineItemResponse=new OrderLineItemResponse();
        InventoryResponse inventoryResponse= webClient.get()
                .uri("http://inventory-service:30002/api/inventories/"+orderLineItem.getInventoryId())
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();
        orderLineItemResponse.setId(orderLineItem.getId());
        orderLineItemResponse.setTotalAmount(orderLineItem.getTotalAmount());
        orderLineItemResponse.setQuantity(orderLineItem.getQuantity());
        orderLineItemResponse.setSkuCode(orderLineItem.getSkuCode());
        orderLineItemResponse.setProductId(inventoryResponse.getProductId());

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
