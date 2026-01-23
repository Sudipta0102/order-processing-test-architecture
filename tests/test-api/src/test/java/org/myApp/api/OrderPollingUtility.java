package org.myApp.api;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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

        AtomicReference<Response> finalResponse = new AtomicReference<>();

        Awaitility.await()
                .atMost(timeout)
                .pollInterval(pollInterval)
                .ignoreExceptions()
                .until(() ->{
                    Response response = RestAssured
                            .given()
                            .when()
                            .get("/orders"+orderId)
                            .then()
                            .extract().response();

                    String status  = response.jsonPath().getString("status");

                    finalResponse.set(response);

                    return "CONFIRMED".equals(status) || "FAILED".equals(status);
                });

        // return the final observed response after awaitility exits
        return finalResponse.get();
    }

    /**
     * Convenience Method.
     *
     * Timeout -> 10sec
     * Poll Every -> 500ms
     */
    public static Response pollUntilTerminal(UUID orderId){
        return pollUntilTerminal(orderId, Duration.ofSeconds(30), Duration.ofMillis(500));
    }

    // These two above methods are overloaded. Essentially the second method is a wrapper.
}
