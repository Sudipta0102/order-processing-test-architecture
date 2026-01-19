package org.myApp.integration;

import io.restassured.RestAssured;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseIntegrationTest {

    protected static final int ORDER_SERVICE_PORT = 8081;
    protected static final int PAYMENT_SERVICE_PORT = 8082;
    protected static final int INVENTORY_SERVICE_PORT = 8083;

    // Header name used to connect dots across services.
    protected static final String ORDER_ID_HEADER = "x-order-id";

    // Maximum time integration tests will wait for async convergence.
    // This value is intentionally generous to accommodate flakiness.
    protected static final Duration MAX_WAIT = Duration.ofSeconds(20);

    // Polling interval
    protected static final Duration POLL_INTERVAL = Duration.ofMillis(500);

    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "http://localhost";
    }

    /**
     *
     * This generated ID is for tracing and troubleshooting
     * (This is different from order id that is created upon order creation)
     * @return random String UUID
     */
    protected String generateOrderCorrelationId(){
        return UUID.randomUUID().toString();
    }

    /**
     *
     * this is Reusable request specification returning method.
     *
     *
     * @return request specification
     */
    protected RequestSpecification  orderServiceRequest(String correlationId){

        RequestSpecification spec = RestAssured.given().port(ORDER_SERVICE_PORT).header(ORDER_ID_HEADER, correlationId);
        QueryableRequestSpecification queryable = SpecificationQuerier.query(spec);
        String headersString = queryable.getHeaders().toString();
        System.out.println("Header: " + headersString);

        return RestAssured
                .given()
                .port(ORDER_SERVICE_PORT)
                .header(ORDER_ID_HEADER, correlationId)
                .contentType("application/json");
    }

    /**
     *
     * @param orderId
     * @return final order status
     */
    protected String waitForFinalOrderStatus(String orderId){


        // Holder for final order status.
        // Because the value will updated be inside Lambda and  that's why it has to be Final,
        // so any mutable holder would do. like String[], AtomicReference<String>
        AtomicReference<String> finalStatus = new AtomicReference<>();

        // Awaitility repeatedly executes the supplied lambda
        // until the condition is satisfied or timeout occurs
        Awaitility.await()
                .atMost(MAX_WAIT)
                .pollInterval(POLL_INTERVAL)
                .until(()-> {

                    // // Call Order Service to fetch current order status
                    String currentStatus =
                            RestAssured
                                    .given()
                                    .port(ORDER_SERVICE_PORT)
                                    .header(ORDER_ID_HEADER, orderId)
                                    .get("/orders/{id}", orderId)
                                    .then()
                                    .statusCode(200)
                                    .extract()
                                    .jsonPath()
                                    .getString("status");

                    // update final status
                    finalStatus.set(currentStatus);

                    // Return true only when order reaches a final state
                    return "CONFIRMED".equals(currentStatus) || "FAILED".equals(currentStatus);


                });

                // return final state anyway.
                return finalStatus.get();

    }

}
