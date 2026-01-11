package org.myApp.inventoryservice.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Request payload for inventory reservation.
 */
public class InventoryRequest {

    @NotBlank
    private String productId;

    @Positive
    private int quantity;

    public InventoryRequest() {

    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
