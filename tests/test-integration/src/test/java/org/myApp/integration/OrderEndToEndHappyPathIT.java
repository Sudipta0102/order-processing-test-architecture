package org.myApp.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
public class OrderEndToEndHappyPathIT extends BaseIntegrationTest{


    @Test
    void testConfirmedOrder(){

        // generating correlation id
        // this is not a business identifier, only for testing purposes
        String correlationId = generateOrderCorrelationId();
        System.out.println("correlation Id :" + correlationId);

        // creating order
        String orderId = orderServiceRequest(correlationId)
                .body("""
                        {
                            "productId" : "A1",
                            "quantity" : 1
                        }
                        """)
                .post("/orders")
                .then()
                .extract()
                .jsonPath()
                .getString("id");

        System.out.println("Order Id: " + orderId);

        // wait until terminal state
        String finalStatus = waitForFinalOrderStatus(orderId);

        Assertions.assertThat(finalStatus)
                .as("Order would be CONFIRMED when inventory has stocks")
                .isEqualTo("CONFIRMED");

    }
}
