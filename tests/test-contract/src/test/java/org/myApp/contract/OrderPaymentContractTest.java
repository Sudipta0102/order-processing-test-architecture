package org.myApp.contract;


import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Contract Test for Order + Payment interaction
 *
 * These tests validate:
 * - endpoint existence
 * - request expectations
 * - response shape
 * - response semantics
 *
 * They do not test:
 * - retries
 * - async behavior
 * - timeouts
 * - flakiness
 */
public class OrderPaymentContractTest extends BaseContractTest {


    // payment service is running on a fixed local port
    private static final int PAYMENT_SERVICE_PORT = 8082;

    @BeforeEach
    void enableDeterministicMode(){

        // Force payment service to be deterministic here
        enableStubMode(PAYMENT_SERVICE_PORT);

    }

    // The contract test executes the real controller in a deterministic mode to validate the service boundary,
    // not its behavior under failure.
    @Test
    void testAcceptPaymentRequestAndReturnSuccessStatus(){

        RestAssured
                .given()
                .port(PAYMENT_SERVICE_PORT)
                .header("x-order-id", "contract-test-order-123")
                .post("/payments")
                .then()
                .statusCode(200)
                .body("paymentStatus", Matchers.equalTo("SUCCESS"));


        // order service sends no request body by design
        // the CONTRACT is to send HTTP 200 on successful processing
        // and response contains payment status
    }


}
