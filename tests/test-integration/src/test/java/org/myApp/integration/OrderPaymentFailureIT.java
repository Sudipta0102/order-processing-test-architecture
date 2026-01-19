package org.myApp.integration;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderPaymentFailureIT extends BaseIntegrationTest{

    @Test
    void testOrderFailsWHenPaymentFails(){

        // generating correlation ID
        String correlationId = generateOrderCorrelationId();

        // creating a new order
        Response response = orderServiceRequest(correlationId)
                .body("""
                              {
                                "productId": "A1",
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
