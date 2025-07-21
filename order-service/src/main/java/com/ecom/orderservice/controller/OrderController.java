package com.ecom.orderservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.ecom.orderservice.dto.InventoryResponse;
import com.ecom.orderservice.dto.OrderLineItemResponse;
import com.ecom.orderservice.dto.OrderPaymentStatusRequest;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.dto.OrderStatusRequest;
import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderLineItem;
import com.ecom.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "order apis")
public class OrderController {

    private final OrderService orderService;
    private final WebClient webClient;
    private static final String INVENTORY_ENDPONT="http://inventory-service:30002/api/inventories/";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest){
        return orderResponseFrom(orderService.createOrder(orderRequest));
    }

    @GetMapping("/{order_id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable("order_id") Long orderId){
        return orderResponseFrom(orderService.getOrder(orderId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrders(){
        return orderService.getOrders().stream().map(this::orderResponseFrom).toList();
    }

    @PatchMapping("/{order_id}/paymentStatus")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updatePaymentStatus(@PathVariable("order_id") Long orderId, @RequestBody OrderPaymentStatusRequest orderPaymentStatusRequest){
        return orderResponseFrom(orderService.updateOrderPaymentStatus(orderId, orderPaymentStatusRequest.getPaymentStatus()));
    }

    @PatchMapping("/{order_id}/status")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateStatus(@PathVariable("order_id") Long orderId, @RequestBody OrderStatusRequest orderStatusRequest){
        return orderResponseFrom(orderService.updateOrderStatus(orderId, orderStatusRequest.getStatus()));
    }

    private OrderResponse orderResponseFrom(Order order){
        OrderResponse orderResponse=new OrderResponse();

        orderResponse.setId(order.getId().toString());
        orderResponse.setOrderLineItemResponses(
            order.getOrderLineItems().stream().map(orderLineItem -> orderLineItemResponseFrom(orderLineItem)).toList()
        );
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setTotalAmount(order.getTotalAmount().toString());
        orderResponse.setPaymentStatus(order.getPaymentStatus());
        orderResponse.setStatus(order.getStatus());
        return orderResponse;
    }
    private OrderLineItemResponse orderLineItemResponseFrom(OrderLineItem orderLineItem){
        OrderLineItemResponse orderLineItemResponse=new OrderLineItemResponse();
        InventoryResponse inventoryResponse= webClient.get()
                .uri(INVENTORY_ENDPONT+orderLineItem.getInventoryId())
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();
        orderLineItemResponse.setId(orderLineItem.getId().toString());
        orderLineItemResponse.setTotalAmount(orderLineItem.getTotalAmount().toString());
        orderLineItemResponse.setQuantity(orderLineItem.getQuantity());
        orderLineItemResponse.setProductId(inventoryResponse.getProductId());

        return orderLineItemResponse;

    }
}
