package org.myApp.integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class OrderPaymentFailureIT extends BaseIntegrationTest{

    @BeforeEach
    void forcingPaymentFailure(){

        RestAssured.given()
                .port(PAYMENT_SERVICE_PORT)
                .contentType("application/json")
                .body(Map.of("mode","ALWAYS_FAIL"))
                .post("/internal/test-mode")
                .then()
                .statusCode(200);

    }

    @AfterEach
    void resetPaymentMode(){

        RestAssured.given()
                .port(PAYMENT_SERVICE_PORT)
                .contentType("application/json")
                .body(Map.of("mode", "NORMAL"))
                .post("/internal/test-mode");
    }

    @Test
    void testOrderFailsWHenPaymentFails(){

        // generating correlation ID
        String correlationId = generateOrderCorrelationId();

        // creating a new order
        Response response = orderServiceRequest(correlationId)
                .body("""
                              {
                                "productId": "C1",
                                "quantity": 1
                              }
                              """)
                .post("/orders")
                .then()
                .extract().response();

        System.out.println(response.asString());

        Assertions.assertThat(response.jsonPath().getString("status"))
                .as("Order should be created with PENDING status")
                .isEqualTo("PENDING");

        String finalStatus = waitForFinalOrderStatus(response.jsonPath().getString("id"));

        Assertions.assertThat(finalStatus)
                .as("Order would be FAILED when payment fails or timeout")
                .isEqualTo("FAILED");

    }
}
