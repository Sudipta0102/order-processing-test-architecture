package org.myApp.orderservice.controller;

import jakarta.validation.Valid;
import org.myApp.orderservice.controller.dto.CreateOrderRequest;
import org.myApp.orderservice.model.Order;
import org.myApp.orderservice.repository.InMemoryOrderRepository;
import org.myApp.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final InMemoryOrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(InMemoryOrderRepository orderRepository, OrderService orderService){
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

//    @GetMapping
//    public String sayHello(){
//        return "late hello from order-service";
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") UUID id){

        Optional<Order> order = orderRepository.findById(id);

        return order
                .map(ResponseEntity::ok) // o->ResponseEntity.ok(o)
                .orElseGet(()->ResponseEntity.notFound().build());

    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request){

        // Request is validated but not used internally. Kept in DTO package.
        Order order = orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);

    }

    @GetMapping
    public ResponseEntity<Collection<Order>> getAllOrders(){

        return ResponseEntity.ok(orderRepository.findAll());
    }

}
