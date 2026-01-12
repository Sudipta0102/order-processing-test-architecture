package org.myApp.orderservice.repository;

import org.myApp.orderservice.model.Order;
import org.myApp.orderservice.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class stores orders in memory
 *
 * - create order
 * - store order
 * - update order status
 * - retrieve orders by Id
 *
 * This repo is:
 * - Thread-safe
 * - not very intelligent
 */
@Repository
public class InMemoryOrderRepository {

    /**
     * Thread-safe map holding orders.
     *
     * Key : Order ID
     * Value : Order object
     *
     * ConcurrentHashMap ensures:
     * - Safe access across async threads
     * - No need for external synchronization
     */
    private final ConcurrentMap<UUID, Order> orders = new ConcurrentHashMap<>();

    /**
     * Create a new order with status PENDING.
     *
     * This method:
     * - Generates a new UUID
     * - Creates a new Order object
     * - Stores it in memory
     * - Returns it to the caller
     */
    public Order create(){

        // generating unique order identifier
        UUID orderId = UUID.randomUUID();

        // creating a new order with PENDING state
        Order order = new Order(orderId, OrderStatus.PENDING);

        // storing order in the map
        orders.put(orderId, order);

        return order;
    }

    /**
     * Update the status of an existing order.
     *
     * This method:
     * - Looks up the order by ID
     * - Updates its status if present
     * - Does nothing if order does not exist
     */
    public void updateStatus(UUID orderId, OrderStatus newStatus){

        // retrieving the order from the map
        Order order = orders.get(orderId);

        // only update if order exists
        if(order != null){
            // updating order status
            order.setStatus(newStatus);
        }
    }

    /**
     * Retrieve an order by its ID.
     *
     * Returns:
     * - Optional containing Order if found
     * - Optional.empty() if not found
     */
    public Optional<Order> findById(UUID orderId){

        // wrapping it in Optional
        return Optional.ofNullable(orders.get(orderId));
    }

    public Collection<Order> findAll(){
        return orders.values();
    }

}
