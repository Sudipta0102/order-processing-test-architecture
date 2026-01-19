package org.myApp.paymentservice.controller;


import org.myApp.paymentservice.controller.dto.TestModeRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal controller used only for tests to force deterministic behavior.
 *
 */
@RestController
@RequestMapping("/internal")
public class InternalTestController {

    // holds current execution mode
    private static final AtomicReference<String> CURRENT_EXEC_MODE = new AtomicReference<>("DEFAULT");

    @PostMapping("/test-mode")
    public void enableTestMode(@RequestBody TestModeRequest request){

        CURRENT_EXEC_MODE.set(request.getMode());

    }

    public static boolean isStubMode(){

        return "STUB".equals(CURRENT_EXEC_MODE.get());

    }
}
