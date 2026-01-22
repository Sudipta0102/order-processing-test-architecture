package org.myApp.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Happy path API test for Order service
 *
 * "Happy Path":
 * - Order created
 * - Async Processing successful
 * - order reaches a terminal state
 *
 */
public class OrderApiHappyPathTest extends BaseApiTest{


    @Test
    public void TestCreateOrderAndReachTerminalState(){

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("productId", "D1");
        requestBody.put("quantity", 1);

        // STEP 1. CREATE ORDER
        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .extract().response();

        System.out.println("Status Code: " + response.getStatusCode());

        // extract orderId from response
        UUID orderId = UUID.fromString(response.jsonPath().getString("id"));
        System.out.println("Created Order Id: " + orderId);

        // Initial status - PENDING
        String initialStatus = response.jsonPath().getString("status");
        System.out.println("Initial Status:" + initialStatus);

        // AssertJ assertions
        Assertions.assertThat(initialStatus)
                .as("order should be created with PENDING status")
                .isEqualTo("PENDING");


        // STEP 2: POLLING

        Response finalResponse = OrderPollingUtility.pollUntilTerminal(orderId);
        //, Duration.ofSeconds(30), Duration.ofMillis(500));

        String finalStatus = finalResponse.jsonPath().getString("status");
        System.out.println("Final Status: " + finalStatus);
        // STEP 3: Assert Terminal State

        Assertions.assertThat(finalStatus)
                .as("final response should CONFIRMED or FAILED")
                .isIn("CONFIRMED", "FAILED");

    }


}
