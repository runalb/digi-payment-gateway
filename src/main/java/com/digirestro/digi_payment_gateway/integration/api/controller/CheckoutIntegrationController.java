package com.digirestro.digi_payment_gateway.integration.api.controller;

import com.digirestro.digi_payment_gateway.auth.service.IntegrationAuthService;
import com.digirestro.digi_payment_gateway.integration.api.dto.CheckoutRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.CheckoutResponse;
import com.digirestro.digi_payment_gateway.integration.api.service.CheckoutIntegrationService;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integration/checkout")
public class CheckoutIntegrationController {

    private final CheckoutIntegrationService checkoutIntegrationService;
    private final IntegrationAuthService integrationAuthService;

    public CheckoutIntegrationController(
            CheckoutIntegrationService checkoutIntegrationService,
            IntegrationAuthService integrationAuthService) {
        this.checkoutIntegrationService = checkoutIntegrationService;
        this.integrationAuthService = integrationAuthService;
    }

    @PostMapping("/generate")
    public ResponseEntity<CheckoutResponse> generateCheckout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequest request) {
        MerchantEntity merchant = integrationAuthService.extractMerchant(authentication);
        CheckoutResponse response = checkoutIntegrationService.generateCheckout(merchant, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
