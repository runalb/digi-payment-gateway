package com.digirestro.digi_payment_gateway.integration.api.controller;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.auth.service.IntegrationAuthService;
import com.digirestro.digi_payment_gateway.integration.channel.service.PaymentOrchestrationService;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integration/transactions")
public class TransactionIntegrationController {

    private final PaymentOrchestrationService paymentOrchestrationService;
    private final IntegrationAuthService integrationAuthService;

    public TransactionIntegrationController(
            PaymentOrchestrationService paymentOrchestrationService,
            IntegrationAuthService integrationAuthService) {
        this.paymentOrchestrationService = paymentOrchestrationService;
        this.integrationAuthService = integrationAuthService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDetailsResponse>> listTransactions(Authentication authentication) {
        MerchantEntity merchant = integrationAuthService.extractMerchant(authentication);
        List<PaymentDetailsResponse> response = paymentOrchestrationService.listPaymentDetails(merchant.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDetailsResponse> getPaymentDetails(
            Authentication authentication,
            @PathVariable("id") Long id) {
        MerchantEntity merchant = integrationAuthService.extractMerchant(authentication);
        PaymentDetailsResponse response = paymentOrchestrationService.getPaymentDetails(id, merchant.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
