package org.myApp.api;


import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Utility class responsible for polling Order Service
 * until an order reaches a terminal state.
 *
 *
 */
public class OrderPollingUtility {


    /**
     *
     * Polls the order service until the order status becomes CONFIRMED or FAILED
     * or timeout
     *
     *
     * @param orderId
     * @param timeout
     * @param pollInterval
     * @return order response
     */
    public static Response pollUntilTerminal(
            UUID orderId,
            Duration timeout,
            Duration pollInterval){

        // Start time of polling
        Instant startTime = Instant.now();

        // polling until timeout
        while (Duration.between(startTime, Instant.now()).compareTo(timeout)<0){

            // perform GET orders/{id}
            Response response = RestAssured
                    .given()
                    .when()
                    .get("/orders/" + orderId)
                    .then()
                    .extract()
                    .response();

            //extract status
            String status = response.jsonPath().getString("status");

            if("CONFIRMED".equals(status) || "FAILED".equals(status)){
                return response;
            }

            // enabling polling interval
            try {
                Thread.sleep(pollInterval.toMillis());
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                throw new RuntimeException("polling interruption", e);
            }

        }

        // timeout is reached, order is still not terminated, fail explicitly instead of hanging forever.
        throw new RuntimeException(
                "Order " + orderId + " didn't reach terminal state"
        );

    }

    /**
     * Convenience Method.
     *
     * Timeout -> 10sec
     * Poll Every -> 500ms
     */
    public static Response pollUntilTerminal(UUID orderId){
        return pollUntilTerminal(orderId, Duration.ofSeconds(10), Duration.ofMillis(500));
    }

    // These two above methods are overloaded. Essentially the second method is a wrapper.
}
