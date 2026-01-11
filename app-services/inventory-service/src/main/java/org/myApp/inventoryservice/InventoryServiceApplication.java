package org.myApp.inventoryservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

/**
 * Entry Point for Inventory service
 *
 * Supposed to be Fast, deterministic,  and stateful (change this comment).
 * Runs on 8083 port by default.
 */
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(InventoryServiceApplication.class);

        application.setDefaultProperties(
                Map.of("server.port", "8083")
        );

        application.run(args);
    }

}
