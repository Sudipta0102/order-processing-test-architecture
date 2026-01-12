package org.myApp.orderservice.service;

import org.myApp.orderservice.model.InventoryResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * Inventory is HTTP client (very thin)
 *
 * What it does:
 * - call Inventory service
 * - Translate HTTP Response into InventoryResult
 *
 */
@Component
public class InventoryClient {


    private final RestTemplate restTemplate = new RestTemplate();

    // hard coded base URL (no config yet)
    private static final String INVENTORY_URL = "http://localhost:8083/inventory/reserve";

    /**
     *
     * @param orderId
     * @return
     *
     * This method makes a
     * - synchronous call
     *
     */
    public InventoryResult reserve(UUID orderId){

        try{
            // Hard-coding inventory inputs initially to isolate the async
            // orchestration before introducing domain complexity
            Map<String, Object> requestBody =  Map.of(
                    "productId", "A1",
                    "quantity", "1"
            );

            // Wrapping request body in HttpEntity
            HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody);

            // Executing Post call to inventory Service
            ResponseEntity<Map> response =
                    restTemplate.exchange(
                            URI.create(INVENTORY_URL),
                            HttpMethod.POST,
                            requestEntity,
                            Map.class
                    );

            // Extracting "status" field from response JSON
            String status = String.valueOf(response.getBody().get("status"));

            // Converting Inventory Response into domain enum
            if("RESERVED".equals(status)){
                return InventoryResult.RESERVED;
            }

            // Any other status is rejection
            return InventoryResult.REJECTED;
        } catch (Exception e) {

            return InventoryResult.REJECTED;
        }

    }

}
