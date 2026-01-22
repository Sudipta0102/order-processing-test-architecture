package org.myApp.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Precondition: Payment service is down
 *
 * Test Flow:
 * - Order is created with PENDING status
 * - Async processing happens and it handles payment failure
 * - Order eventually transitions to FAILED
 */
public class OrderApiPaymentFailureTest extends BaseApiTest{

    @Test
    public void TestCreateOrderWithPaymentServiceDown(){

        Map<String , Object> requestBody = new HashMap<>();
        requestBody.put("productId", "F1");
        requestBody.put("quantity", 1);

        // 1. CREATE ORDER
        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .extract().response();

        // Extract order ID
        UUID orderId = UUID.fromString(response.jsonPath().getString("id"));
        System.out.println("Created Order ID: " + orderId);

        // Initial status is PENDING
        String initialStatus = response.jsonPath().getString("status");
        System.out.println("Initial Status: " + initialStatus);

        // assert it is PENDING
        Assertions.assertThat(initialStatus)
                .as("Order is created with PENDING status")
                .isEqualTo("PENDING");


        // 2. POLLING UNTIL TERMINAL
        Response finalResponse = OrderPollingUtility.pollUntilTerminal(orderId);
        //, Duration.ofSeconds(20), Duration.ofMillis(500));

        String finalStatus = finalResponse.jsonPath().getString("status");


        //System.out.println(response.asString());
        // 3. ASSERT FAILED
        Assertions.assertThat(finalStatus)
                .as("Final Status is failed because Payment Service is unavailable")
                .isEqualTo("FAILED");
    }
}
