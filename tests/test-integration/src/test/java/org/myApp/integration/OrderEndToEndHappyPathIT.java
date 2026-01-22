package org.myApp.integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * End to end happy path
 *
 * it does:
 * - order creation
 * - async processing
 * - The system reaches a terminal status CONFIRMED
 *
 *
 */
@Tag("integration")
public class OrderEndToEndHappyPathIT extends BaseIntegrationTest{


    @Test
    void testConfirmedOrder(){

        // generating correlation id
        // this is not a business identifier, only for testing purposes
        String correlationId = generateOrderCorrelationId();
        System.out.println("correlation Id :" + correlationId);

        // creating order
        Response response = orderServiceRequest(correlationId)
                .body(Map.of("productId", "A1", "quantity", 1))
                .post("/orders")
                .then()
                .extract().response();

        System.out.println("Order Creation response: " + response.asString());

        // wait until terminal state
        String finalStatus = waitForFinalOrderStatus(response.jsonPath().getString("id"));

        System.out.println("Final Status from GET endpoint:" + finalStatus);

        Assertions.assertThat(finalStatus)
                .as("Order would be CONFIRMED when inventory has stocks")
                .isEqualTo("CONFIRMED");

    }
}
