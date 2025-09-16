package com.ecom.orderservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.orderservice.client.InventoryServiceClient;
import com.ecom.orderservice.client.ProductServiceClient;
import com.ecom.orderservice.dto.Inventory;
import com.ecom.orderservice.dto.InventoryClaim;
import com.ecom.orderservice.dto.OrderLineItemRequest;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.Product;
import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderLineItem;
import com.ecom.orderservice.model.OrderPaymentStatus;
import com.ecom.orderservice.model.OrderStatus;
import com.ecom.orderservice.repository.OrderRepository;
import com.ecom.orderservice.utils.IDGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final IDGenerator idGenerator;
    private final Logger logger;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    public Order createOrder(OrderRequest orderRequest){

        Order order=new Order();
        order.setId(idGenerator.next());
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setTotalAmount(new BigDecimal(0));

        List<OrderLineItem> orderLineItems= orderRequest.getOrderLineItemRequests().stream().map(
            orderLineItemRequest -> orderLineItemFrom(orderLineItemRequest, order) ).toList();
        order.setOrderLineItems(orderLineItems);

        order.setStatus(OrderStatus.ORDER_STATUS_CREATED);
        order.setPaymentStatus(OrderPaymentStatus.ORDER_PAYMENT_STATUS_PENDING);
        claimInventory(orderLineItems);
        return orderRepository.save(order);
    }

    private void claimInventory(List<OrderLineItem> orderLineItems){

        for(OrderLineItem orderLineItem: orderLineItems){
            InventoryClaim inventoryClaim=inventoryServiceClient.claimInventory(orderLineItem.getInventoryId(), orderLineItem.getQuantity());
            orderLineItem.setInventoryClaimId(Long.valueOf(inventoryClaim.getClaimId()));
        }

    }

    private void markClaimedInventory(List<OrderLineItem> orderLineItems){
        for(OrderLineItem orderLineItem: orderLineItems){
            inventoryServiceClient.markClaimedInventory(orderLineItem.getInventoryClaimId());
        }

    }

    @Transactional
    public Order getOrder(Long orderId){
        Optional<Order> order=orderRepository.findById(orderId);
        if(!order.isPresent()){
            return null;
        }
        return order.get();
    }

    public List<Order> getOrders(){
        return orderRepository.findAll();
    }


    @Transactional
    public Order updateOrderPaymentStatus(Long orderId, OrderPaymentStatus paymentStatus){
        Order order=orderRepository.findById(orderId).get();
        if(order.getPaymentStatus()==paymentStatus){
            return order;
        }
        markClaimedInventory(order.getOrderLineItems());
        if(!validateUpdateOrderPaymentStatus(order, paymentStatus)){
            logger.error("order payment status update not allowed");
            return null;
        }
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus){
        Order order=orderRepository.findById(orderId).get();
        if(!validateUpdateOrderStatus(order, orderStatus)){
            logger.error("order status update not allowed");
            return null;
        }
        order.setStatus(orderStatus);
        return orderRepository.save(order);
    }

    private boolean validateUpdateOrderStatus(Order order, OrderStatus newOrderStatus){
        switch (order.getStatus()) {
            case ORDER_STATUS_CREATED:
                if(newOrderStatus==OrderStatus.ORDER_STATUS_COMPLETE || newOrderStatus==OrderStatus.ORDER_STATUS_CANCELLED){
                    return true;
                }
                break;
            case ORDER_STATUS_CANCELLED:
                break;
            case ORDER_STATUS_COMPLETE:
                break;
            default:
                break;
        }
        return false;
    }

    private boolean validateUpdateOrderPaymentStatus(Order order, OrderPaymentStatus newOrderPaymentStatus){
        switch (order.getPaymentStatus()) {
            case ORDER_PAYMENT_STATUS_INITIATED:
                if(newOrderPaymentStatus==OrderPaymentStatus.ORDER_PAYMENT_STATUS_COMPLETED){
                    return true;
                }
                break;
            case OrderPaymentStatus.ORDER_PAYMENT_STATUS_PENDING:
                if(newOrderPaymentStatus==OrderPaymentStatus.ORDER_PAYMENT_STATUS_INITIATED){
                    return true;
                }
                break;
            case OrderPaymentStatus.ORDER_PAYMENT_STATUS_COMPLETED:
                if(newOrderPaymentStatus==OrderPaymentStatus.ORDER_PAYMENT_STATUS_REFUND_INITIATED){
                    return true;
                }
                break;
            case OrderPaymentStatus.ORDER_PAYMENT_STATUS_REFUND_INITIATED:
                if(newOrderPaymentStatus==OrderPaymentStatus.ORDER_PAYMENT_STATUS_REFUNDED){
                    return true;
                }
                break;
        
            default:
                break;
        }
        return false;
    }

    private OrderLineItem orderLineItemFrom(OrderLineItemRequest orderLineItemRequest, Order order){
        
        Inventory inventory=inventoryServiceClient.getInventoryById(orderLineItemRequest.getInventoryId());
        Product product=productServiceClient.getProductById(inventory.getProductId());

        OrderLineItem orderLineItem=new OrderLineItem();
        
        BigDecimal lineItemTotal=product.getPrice().multiply(BigDecimal.valueOf(orderLineItemRequest.getQuantity()));

        orderLineItem.setId(idGenerator.next());
        orderLineItem.setTotalAmount(lineItemTotal);
        orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
       
        orderLineItem.setOrder(order);

        orderLineItem.setInventoryId(Long.valueOf(orderLineItemRequest.getInventoryId()));
        
        order.setTotalAmount(lineItemTotal.add(order.getTotalAmount()));

        return orderLineItem;
    }
}
