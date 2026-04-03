package com.digirestro.digi_payment_gateway.controller.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Placeholder UI routes. Merchants: {@link MerchantController}; users: {@link UserController}. */
@RestController
@RequestMapping("/api/v1/ui")
public class UiController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        String response = "UI test API";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
}
