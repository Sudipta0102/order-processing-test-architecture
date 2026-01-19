package org.myApp.contract;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

/**
 * Base class for all contract tests.
 *
 * This class provides:
 * - Common Rest Assured config
 * - deterministic stub-mode for dependent services
 * - shared request set up (headers, content type)
 *
 *
 */
public abstract class BaseContractTest {

    protected static final String ORDER_ID_HEADER = "X_ORDER_ID";

    @BeforeAll
    static void setup(){

        RestAssured.baseURI = "http://localhost";

        // ports are not set here. Each contract test specifies the target service port.

    }

    /**
     * 1. Enables deterministic stub mode for a target service
     * 2. This is NOT mocking from the outside.
     * 3. THe service itself owns how it switches into stub mode
     *
     *
     * @param port
     */
    protected void enableStubMode(int port){

        RestAssured.given()
                .port(port)                             // Explicit port of the target service
                .body(Map.of("mode", "STUB"))    // Simple request payload to enable Stub mode
                .post("/internal/test-mode")          // Test-only Endpoint exposed by the service
                .then()
                .statusCode(200);

    }

    protected RequestSpecification baseRequest(int port){
        // Provides a common request specification for contract test
        // Keeps headers and content-type consistent.

        return RestAssured
                .given()
                .port(port)
                .header(ORDER_ID_HEADER, "contract-test-order-123") // adding correlation ID header and fixed value for determinism
                .contentType("application/json");  // always JSON

    }
}
