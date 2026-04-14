package com.digirestro.digi_payment_gateway.integration.controller;

import com.digirestro.digi_payment_gateway.integration.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.integration.service.IntegrationAuthenticationService;
import com.digirestro.digi_payment_gateway.integration.service.PaymentOrchestrationService;
import com.digirestro.digi_payment_gateway.portal.merchant.entity.MerchantEntity;

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
    private final IntegrationAuthenticationService integrationAuthenticationService;

    public TransactionIntegrationController(
            PaymentOrchestrationService paymentOrchestrationService,
            IntegrationAuthenticationService integrationAuthenticationService) {
        this.paymentOrchestrationService = paymentOrchestrationService;
        this.integrationAuthenticationService = integrationAuthenticationService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDetailsResponse>> listTransactions(Authentication authentication) {
        MerchantEntity merchant = integrationAuthenticationService.extractMerchant(authentication);
        List<PaymentDetailsResponse> response = paymentOrchestrationService.listPaymentDetails(merchant.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDetailsResponse> getPaymentDetails(
            Authentication authentication,
            @PathVariable("id") Long id) {
        MerchantEntity merchant = integrationAuthenticationService.extractMerchant(authentication);
        PaymentDetailsResponse response = paymentOrchestrationService.getPaymentDetails(id, merchant.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
