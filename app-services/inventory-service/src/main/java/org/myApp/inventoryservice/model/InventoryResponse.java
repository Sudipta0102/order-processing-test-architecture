package org.myApp.inventoryservice.model;

/**
 * Response returned after attempting to reserve inventory.
 */
public class InventoryResponse {

    private InventoryStatus status;

    public InventoryResponse() {
    }

    public InventoryResponse(InventoryStatus status) {
        this.status = status;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }
}
