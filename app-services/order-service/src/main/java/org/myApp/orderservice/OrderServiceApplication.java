package org.myApp.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for Payment Service.
 *
 * It runs on port 8082 by default.
 */
@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(OrderServiceApplication.class);

        Map<String, Object> defaultProperties = new HashMap<>();
        defaultProperties.put("server.port", "8081");
        application.setDefaultProperties(
                defaultProperties
        );

        application.run(args);
    }
}
