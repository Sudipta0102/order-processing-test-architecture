package org.myApp.orderservice.service.dto;

public class PaymentResponseDto {

    private String paymentStatus;

    public String getPaymentStatus() {

        return paymentStatus;
    }

    public void setPaymentStatus(String status) {

        this.paymentStatus = status;
    }
}
