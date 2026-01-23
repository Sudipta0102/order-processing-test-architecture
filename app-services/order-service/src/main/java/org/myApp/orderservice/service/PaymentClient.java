package org.myApp.orderservice.service;

import org.myApp.orderservice.model.PaymentResult;
import org.myApp.orderservice.service.dto.PaymentResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 *
 * Payment is HTTP client (Thin, very thin)
 * - call Payment Service
 * - translate non-deterministic, flaky outcomes of Payment Service into PaymentResult
 *
 */
@Component
public class PaymentClient {


    // RestTemplate performs blocking HTTP calls.
    // Blocking works because this runs in a background thread
    private final RestTemplate restTemplate;

    private final String PAYMENT_URL;
    // Hard-coded payment service URL for docker
    // for local: "http://localhost:8082/payments";
    //private static final String PAYMENT_URL = "http://payment-service:8082/payments";


    public PaymentClient(@Value("${payment.base-url}") String PAYMENT_URL){

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(2000);
        factory.setReadTimeout(2000);

        this.restTemplate = new RestTemplate(factory);
        this.PAYMENT_URL = PAYMENT_URL;
    }

    /**
     *
     * @param orderId
     * @return
     *
     * Triggering Payments for an order
     * This method does
     * - synchronous call
     * - handling timeouts
     */
    public PaymentResult pay(UUID orderId){

        try{
            // empty request body
            // Does not require a payload
            HttpEntity<Void> requestEntity = new HttpEntity<>(null);

            // Execute Http POST to payment service
            ResponseEntity<PaymentResponseDto> response =
                    restTemplate.exchange(
                            URI.create(PAYMENT_URL),
                            HttpMethod.POST,
                            requestEntity,
                            PaymentResponseDto.class
                    );

            if(response.getBody() == null){
                return PaymentResult.FAILED;
            }

            // Extract "paymentStatus" field from response JSON
            //String status = String.valueOf(response.getBody().get("paymentStatus"));

            // translating response into domain result
            if("SUCCESS".equals(response.getBody().getPaymentStatus())){
                return PaymentResult.SUCCESS;
            }

            // anything else is failure
            return PaymentResult.FAILED;

        }catch(ResourceAccessException timeoutException){

            // 1. Connection timeout
            // 2. Payment service hanging

            return PaymentResult.TIMEOUT;
        } catch (Exception e) {

            // 1. HTTP 500
            // 2. JSON parsing error
            // etc etc
            return PaymentResult.FAILED;
        }
    }
}
