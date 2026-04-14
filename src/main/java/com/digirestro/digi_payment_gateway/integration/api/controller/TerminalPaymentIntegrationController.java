package com.digirestro.digi_payment_gateway.integration.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// use in future - not used yet - placeholder
@RestController
@RequestMapping("/api/v1/integration/terminal-payment")
public class TerminalPaymentIntegrationController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        String response = "Terminal payment test API";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
