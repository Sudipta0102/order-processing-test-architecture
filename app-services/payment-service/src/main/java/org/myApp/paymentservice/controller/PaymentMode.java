package org.myApp.paymentservice.controller;

public enum PaymentMode {

    NORMAL,          // Default behavior: flaky, real-world simulation
    STUB,            // Always succeed (used for contract tests)
    ALWAYS_FAIL,     // Always return HTTP 500
    ALWAYS_TIMEOUT;  // Always exceed client timeout

    public static PaymentMode from(String val){
        return PaymentMode.valueOf(val.toUpperCase());
    }

}
