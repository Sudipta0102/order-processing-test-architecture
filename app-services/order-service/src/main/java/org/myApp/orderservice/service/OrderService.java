package org.myApp.orderservice.service;


/**
 * OrderService will be taking care of:
 *
 * - creating orders
 * - Running Async Orchestration
 * - Calling Inventory and Payment services
 * - Deciding final order state
 */

import org.myApp.orderservice.model.InventoryResult;
import org.myApp.orderservice.model.Order;
import org.myApp.orderservice.model.OrderStatus;
import org.myApp.orderservice.model.PaymentResult;
import org.myApp.orderservice.repository.InMemoryOrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OrderService {

    // Repository that stores orders in memory
    private final InMemoryOrderRepository orderRepository;

    // client for calling Inventory service
    private final InventoryClient inventoryClient;

    // Client for calling payment service
    private final PaymentClient paymentClient;

    /**
     * Single executor for order processing.
     * No sharing across services.
     *
     * Executor service is used to run order processing in the background
     *
     * Fixed Thread Pool:
     * - Limit Concurrency
     * - Prevents unbounded thread creation
     *
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public OrderService(InMemoryOrderRepository orderRepository, InventoryClient inventoryClient, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
    }

    /**
     * Create order and immediately return PENDING
     * Background processing would be triggered after that.
     *
     * This method:
     * - return immediately
     * - start async processing
     */
    public Order createOrder(){

        // Create a new order with status PENDING
        Order order = orderRepository.create();

        log(order.getId(), "CREATED", null, null, OrderStatus.PENDING);

        // now the order is created and logged, async processing starts.
        executor.submit(()-> processOrder(order.getId()));

        return order;
    }

    /**
     *
     * @param orderId
     * @return
     *
     * Background logic.
     * Runs asynchronously on a thread from ExecutorService
     */
    private void processOrder(UUID orderId){

        InventoryResult inventoryResult = null;
        PaymentResult paymentResult = null;

        try{
            // 1. CALLING AND CHECKING INVENTORY (this is concrete, deterministic)

            // calling inventory service to reserve the stock
            inventoryResult = inventoryClient.reserve(orderId);

            // order must fail if inventory does not have the required quantity
            if(inventoryResult == InventoryResult.REJECTED){

                // updating order status is failed
                orderRepository.updateStatus(orderId, OrderStatus.FAILED);

                // log why the order failed - INVENTORY_REJECTED
                log(orderId, "INVENTORY_REJECTED", inventoryResult, null, OrderStatus.FAILED);

                return;
            }

            // 2. CALLING PAYMENT (this is flaky by design)

            // call payment service (this can be anything: Success, Fail, Timeout)

            paymentResult = paymentClient.pay(orderId);

            // if inventory reserved and inventory reserve was a success
            if(paymentResult == PaymentResult.SUCCESS){

                // Update order status as CONFIRMED
                orderRepository.updateStatus(orderId, OrderStatus.CONFIRMED);

                log(orderId, "CONFIRMED", inventoryResult, paymentResult, OrderStatus.CONFIRMED);

            }else{
                // Payment failed or timed out

                // update order status as FAILED
                orderRepository.updateStatus(orderId, OrderStatus.FAILED);

                // failure reason is Payment Failed
                log(orderId, "PAYMENT_FAILED", inventoryResult, paymentResult, OrderStatus.FAILED);
            }
        }catch(Exception e){
            // Catch all exceptions to guarantee no order stays in PENDING forever

            // just to be safe, mark order as failed
            orderRepository.updateStatus(orderId, OrderStatus.FAILED);

            // failure reason is =- EXCEPTION
            log(orderId, "EXCEPTION", inventoryResult, paymentResult, OrderStatus.FAILED);
        }

    }

    /**
     *
     * @param orderId
     * @param step
     * @param inventoryResult
     * @param paymentResult
     * @param finalStatus
     *
     * Logging method
     */
    private void log(UUID orderId,
                     String step,
                     InventoryResult inventoryResult,
                     PaymentResult paymentResult,
                     OrderStatus finalStatus) {

        System.out.println(
                "[ORDER] orderId=" + orderId +
                        " step=" + step +
                        " inventory=" + inventoryResult +
                        " payment=" + paymentResult +
                        " finalStatus=" + finalStatus
        );
    }
}
