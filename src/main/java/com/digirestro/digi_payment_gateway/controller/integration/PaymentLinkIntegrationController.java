package com.digirestro.digi_payment_gateway.controller.integration;

import com.digirestro.digi_payment_gateway.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.service.PaymentOrchestrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integration/payment-links")
public class PaymentLinkIntegrationController {

    private final PaymentOrchestrationService paymentOrchestrationService;

    public PaymentLinkIntegrationController(PaymentOrchestrationService paymentOrchestrationService) {
        this.paymentOrchestrationService = paymentOrchestrationService;
    }

    @PostMapping("/create")
    public PaymentLinkResponse createPaymentLink(@Valid @RequestBody PaymentLinkRequest request) {
        return paymentOrchestrationService.createPaymentLink(request);
    }

    @GetMapping("/{id}")
    public PaymentDetailsResponse getPaymentDetails(@PathVariable("id") Long id) {
        return paymentOrchestrationService.getPaymentDetails(id);
    }
}
