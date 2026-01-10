package org.myApp.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * This controller simulates an unreliable external dependency.
 *
 * Behavior is intentionally non-deterministic:
 *  - Sometimes succeeds
 *  - Sometimes fails
 *  - Sometimes hangs (timeout)
 *
 * This service is designed to create flakiness
 * that higher-level tests must handle.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final Random random = new Random();

    /**
     * Process a payment request.
     *
     * No request body on purpose.
     * Order ID is passed via header only for logging.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> processPayment(@RequestHeader(value = "x-order-id", required = false)
                                                              String orderIdHeader){

        // If order id is absent, generate one for logging
        String orderId = orderIdHeader!=null ? orderIdHeader : UUID.randomUUID().toString();

        // Random number between 0 and 99
        int outcome = random.nextInt(100);

        try {
            /*
             * 0–69   → SUCCESS (70%)
             * 70–89  → TIMEOUT (20%)
             * 90–99  → HTTP 500 (10%)
             */
            //SUCCESS
            if (outcome < 70) {

                // Random short delay to simulate network / processing time
                int delay = random.nextInt(500) + 100;

                log(orderId, "SUCCESS", delay);

                Thread.sleep(delay);
                return ResponseEntity.ok(Map.of("paymentStatus", "SUCCESS"));

            } else if (outcome < 90) { // TIMEOUT
                //sleep longer than any reasonable client timeout
                int delay = 5000;

                log(orderId, "TIMEOUT", delay);

                Thread.sleep(delay);

                // client should timeout before receiving this. Did the payment go through or not. No, it didn't.
                return ResponseEntity.ok(Map.of("paymentStatus", "SUCCESS"));
            } else {
                // FAILURE PATH (HTTP 500)
                log(orderId, "HTTP_500", 0);

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();

            }

        }catch(InterruptedException e){
            // Restore interrupt flag and fail fast
            Thread.currentThread().interrupt();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Simple console logging.
     *
     */
    private void log(String orderId, String behavior, int delay){
        System.out.println(
                "[PAYMENT] orderId=" + orderId +
                        " behavior=" + behavior +
                        " delayMs=" + delay
        );
    }

}


