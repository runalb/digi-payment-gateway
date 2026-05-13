package com.digirestro.digi_payment_gateway.integration.api.controller;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.auth.service.IntegrationAuthService;
import com.digirestro.digi_payment_gateway.integration.api.service.PaymentLinkIntegrationService;
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
@RequestMapping("/api/v1/integration/payment-link")
public class PaymentLinkIntegrationController {

    private final PaymentLinkIntegrationService paymentLinkIntegrationService;
    private final IntegrationAuthService integrationAuthService;

    public PaymentLinkIntegrationController(
            PaymentLinkIntegrationService paymentLinkIntegrationService,
            IntegrationAuthService integrationAuthService) {
        this.paymentLinkIntegrationService = paymentLinkIntegrationService;
        this.integrationAuthService = integrationAuthService;
    }

    @PostMapping("/generate")
    public ResponseEntity<PaymentLinkResponse> generatePaymentLink(
            Authentication authentication,
            @Valid @RequestBody PaymentLinkRequest request) {
        MerchantEntity merchant = integrationAuthService.extractMerchant(authentication);
        PaymentLinkResponse response = paymentLinkIntegrationService.generatePaymentLink(merchant, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
