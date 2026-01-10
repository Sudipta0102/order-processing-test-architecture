package org.myApp.model;

import java.util.UUID;

public class Order {

    private UUID id;
    private OrderStatus status;

    public Order() {
        // Default constructor for JSON serialization
    }

    public Order(UUID id, OrderStatus status) {
        this.id = id;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

}
