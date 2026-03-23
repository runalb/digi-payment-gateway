package com.digirestro.digi_payment_gateway.controller.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// use in future - not used yet - placeholder
@RestController
@RequestMapping("ui/api/v1")
public class UiController {

    @GetMapping("/test")
    public String test() {
        return "UI test API";
    }

    
}
