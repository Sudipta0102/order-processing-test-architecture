package org.myApp.contract;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * Contract tests for order + inventory interaction
 *
 * These tests validate:
 * - endpoint existence
 * - request payload shape
 * - deterministic response semantics
 *
 * Inventory is deterministic by design, So no stub mode toggling is required.
 */
@Tag("contract")
public class OrderInventoryContractTest extends BaseContractTest{

    // inventory service runs on a fixed local port
    private static final int INVENTORY_SERVICE_PORT = 8083;

    @Test
    void testReserveInventoryWhenStockIsAvailable(){

        baseRequest(INVENTORY_SERVICE_PORT)
                .body(Map.of(
                        "productId","G1",
                        "quantity", 1
                ))
                .post("/inventory/reserve")
                .then()
                .statusCode(200)
                .body("status", Matchers.equalTo("RESERVED"));
        // The CONTRACT is to send HTTP 200 on successful reservation
        // and response contains the reservation status (inventoryStatus)
    }

    @Test
    void testRejectInventoryWhenStockIsInsufficient(){

        baseRequest(INVENTORY_SERVICE_PORT)
                .body(Map.of(
                        "productId","A1",
                        "quantity", 200
                ))
                .post("/inventory/reserve")
                .then()
                .statusCode(200)
                .body("status", Matchers.equalTo("OUT_OF_STOCK"));

        // The CONTRACT is to send HTTP 200 even when reservation is rejected (semantic failure, not transport failure)
        // but response explicitly signals rejection (inventoryStatus)

    }
}
