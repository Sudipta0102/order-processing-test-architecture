package org.myApp.integration;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
public class OrderInventoryFailureIT extends BaseIntegrationTest{

    @Test
    void testOrderFailsWhenInventoryInsufficient(){

        // generating correlation ID
        String correlationId = generateOrderCorrelationId();

        // create an order where high quantity
        Response response = orderServiceRequest(correlationId)
                .body("""
                              {
                                "productId": "B1",
                                "quantity": 999
                              }
                              """)
                .post("orders")
                .then()
                .extract().response();

        System.out.println("Order Creation Response: " + response.asString());

        Assertions.assertThat(response.jsonPath().getString("status"))
                .as("Order should be created with PENDING status")
                .isEqualTo("PENDING");

        String finalStatus = waitForFinalOrderStatus(response.jsonPath().getString("id"));

        Assertions.assertThat(finalStatus)
                .as("Order must FAIL when inventory reservation is rejected")
                .isEqualTo("FAILED");

    }
}
