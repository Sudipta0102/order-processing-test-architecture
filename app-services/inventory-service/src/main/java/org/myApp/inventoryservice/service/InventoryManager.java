package org.myApp.inventoryservice.service;

import org.myApp.inventoryservice.model.InventoryRequest;
import org.myApp.inventoryservice.model.InventoryResponse;
import org.myApp.inventoryservice.model.InventoryStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InventoryManager holds in-memory inventory state
 * and applies deterministic reservation rules.
 */
@Service
public class InventoryManager {
    /**
     * In-memory stock store.
     *
     * Key   → productId
     * Value → available quantity
     */
    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    public InventoryManager(){
        // some hardcoded stock.
        stock.put("A1", 100);
        stock.put("B1", 9);
        stock.put("C1", 10);
    }

    /**
     * Reserve inventory if available.
     *
     * Rules:
     * - If requested quantity <= available stock → RESERVED
     * - Otherwise → OUT_OF_STOCK
     */
    public InventoryResponse reserve(InventoryRequest inventoryRequest){

        String productId = inventoryRequest.getProductId();
        int requestedQty = inventoryRequest.getQuantity();

        int availableQty = stock.getOrDefault(productId, 0);

        if(requestedQty<=availableQty){
            //reduce stock
            stock.put(productId, availableQty-requestedQty);

            return new InventoryResponse(InventoryStatus.RESERVED);
        }

        return new InventoryResponse(InventoryStatus.OUT_OF_STOCK);
    }
}
