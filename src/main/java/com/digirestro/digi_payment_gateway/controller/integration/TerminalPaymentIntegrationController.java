package com.digirestro.digi_payment_gateway.controller.integration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// use in future - not used yet - placeholder
@RestController
@RequestMapping("/api/v1/integration/terminal-payments")
public class TerminalPaymentIntegrationController {

    @GetMapping("/test")
    public String test() {
        return "Terminal payment test API";
    }

}
