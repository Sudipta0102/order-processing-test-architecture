package org.myApp.paymentservice.controller;


import org.myApp.paymentservice.controller.dto.TestModeRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal controller used only for tests to force deterministic behavior.
 *
 */
@RestController
@RequestMapping("/internal")
public class InternalTestController {

    // holds current execution mode
    private static final AtomicReference<PaymentMode> CURRENT_EXEC_MODE = new AtomicReference<>(PaymentMode.NORMAL);

    @PostMapping(
            value = "/test-mode",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void enableTestMode(@RequestBody TestModeRequest request){

        try {
            CURRENT_EXEC_MODE.set(PaymentMode.from(request.getMode()));
        }catch(IllegalArgumentException e){
            // For invalid values other than what is mentioned in PaymentMode enum
            // I don't want to crash the test because of this.
            System.out.println("[PAYMENT][TEST-MODE] Invalid mode received: " + request.getMode());
        }
    }

    public static boolean isStubMode(){

        return PaymentMode.STUB.equals(CURRENT_EXEC_MODE.get());
        //return "STUB".equals(CURRENT_EXEC_MODE.get());

    }

    public static boolean isAlwaysFail() {
        return PaymentMode.ALWAYS_FAIL.equals(CURRENT_EXEC_MODE.get());
    }

    public static boolean isAlwaysTimeout() {
        return PaymentMode.ALWAYS_TIMEOUT.equals(CURRENT_EXEC_MODE.get());
    }

    public static boolean isNormalMode() {
        return PaymentMode.NORMAL.equals(CURRENT_EXEC_MODE.get());
    }
}
