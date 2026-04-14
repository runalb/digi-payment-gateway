package com.digirestro.digi_payment_gateway.integration.controller;

import com.digirestro.digi_payment_gateway.integration.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.integration.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.integration.service.IntegrationAuthenticationService;
import com.digirestro.digi_payment_gateway.integration.service.PaymentOrchestrationService;
import com.digirestro.digi_payment_gateway.portal.merchant.entity.MerchantEntity;

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

    private final PaymentOrchestrationService paymentOrchestrationService;
    private final IntegrationAuthenticationService integrationAuthenticationService;

    public PaymentLinkIntegrationController(
            PaymentOrchestrationService paymentOrchestrationService,
            IntegrationAuthenticationService integrationAuthenticationService) {
        this.paymentOrchestrationService = paymentOrchestrationService;
        this.integrationAuthenticationService = integrationAuthenticationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<PaymentLinkResponse> generatePaymentLink(
            Authentication authentication,
            @Valid @RequestBody PaymentLinkRequest request) {
        MerchantEntity merchant = integrationAuthenticationService.extractMerchant(authentication);
        PaymentLinkResponse response = paymentOrchestrationService.generatePaymentLink(merchant, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
