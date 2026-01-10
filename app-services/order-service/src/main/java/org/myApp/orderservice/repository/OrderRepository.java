package org.myApp.orderservice.repository;

import org.myApp.orderservice.model.CreateOrderRequest;
import org.myApp.orderservice.model.Order;
import org.myApp.orderservice.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepository {

    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    public Order createOrder(CreateOrderRequest request){

        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, OrderStatus.PENDING);

        orders.put(orderId, order);

        return order;
    }

    public Optional<Order> findById(UUID id){
        return Optional.ofNullable(orders.get(id));
    }

    public Collection<Order> findAll(){
        return orders.values();
    }
}
