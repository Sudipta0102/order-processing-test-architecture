package org.myApp.controller;

import jakarta.validation.Valid;
import org.myApp.model.CreateOrderRequest;
import org.myApp.model.Order;
import org.myApp.repository.OrderRepository;
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

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
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

        Order order = orderRepository.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);

    }

    @GetMapping
    public ResponseEntity<Collection<Order>> getAllOrders(){
        return ResponseEntity.ok(orderRepository.findAll());
    }

}
