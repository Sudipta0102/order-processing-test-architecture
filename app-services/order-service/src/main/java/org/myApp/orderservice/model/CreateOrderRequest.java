package org.myApp.orderservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CreateOrderRequest {

    @NotBlank
    private String productId;

    @Positive
    private int quantity;

    public CreateOrderRequest(){}

    public String getProductId(){
        return productId;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setProductId(String productId){
        this.productId = productId;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
}
