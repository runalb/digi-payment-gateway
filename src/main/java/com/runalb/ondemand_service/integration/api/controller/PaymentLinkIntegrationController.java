package com.runalb.ondemand_service.integration.api.controller;

import com.runalb.ondemand_service.integration.api.dto.PaymentLinkRequest;
import com.runalb.ondemand_service.integration.api.dto.PaymentLinkResponse;
import com.runalb.ondemand_service.auth.service.IntegrationAuthService;
import com.runalb.ondemand_service.integration.channel.service.PaymentOrchestrationService;
import com.runalb.ondemand_service.merchant.entity.MerchantEntity;

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
    private final IntegrationAuthService integrationAuthService;

    public PaymentLinkIntegrationController(
            PaymentOrchestrationService paymentOrchestrationService,
            IntegrationAuthService integrationAuthService) {
        this.paymentOrchestrationService = paymentOrchestrationService;
        this.integrationAuthService = integrationAuthService;
    }

    @PostMapping("/generate")
    public ResponseEntity<PaymentLinkResponse> generatePaymentLink(
            Authentication authentication,
            @Valid @RequestBody PaymentLinkRequest request) {
        MerchantEntity merchant = integrationAuthService.extractMerchant(authentication);
        PaymentLinkResponse response = paymentOrchestrationService.generatePaymentLink(merchant, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
