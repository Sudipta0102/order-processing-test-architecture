package org.myApp.orderservice.model;

import java.util.UUID;

public class Order {

    private UUID id;
    private OrderStatus status;
    private String productId;
    private int quantity;

    public Order() {}

    public Order(UUID id, OrderStatus status, String productId, int quantity) {
        this.id = id;
        this.status = status;
        this.productId = productId;
        this.quantity = quantity;
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

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
