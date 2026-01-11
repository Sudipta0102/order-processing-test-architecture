package org.myApp.inventoryservice.controller;

import jakarta.validation.Valid;
import org.myApp.inventoryservice.model.InventoryRequest;
import org.myApp.inventoryservice.model.InventoryResponse;
import org.myApp.inventoryservice.service.InventoryManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inventory Controller.
 *
 * Exposes a deterministic API to reserve inventory.
 * This service is intentionally reliable and fast.
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryManager inventoryManager;


    public InventoryController(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    /**
     * Reserve inventory for a product.
     *
     * Endpoint:
     * POST /inventory/reserve
     */
    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserveInventory(@Valid @RequestBody InventoryRequest inventoryRequest){

        InventoryResponse inventoryResponse = inventoryManager.reserve(inventoryRequest);

        return ResponseEntity.ok(inventoryResponse);

    }

}
