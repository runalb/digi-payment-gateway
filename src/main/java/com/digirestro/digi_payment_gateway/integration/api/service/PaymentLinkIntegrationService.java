package com.digirestro.digi_payment_gateway.integration.api.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.payment.service.PaymentOrchestrationService;

import org.springframework.stereotype.Service;

@Service
public class PaymentLinkIntegrationService {

    private final PaymentOrchestrationService paymentOrchestrationService;

    public PaymentLinkIntegrationService(PaymentOrchestrationService paymentOrchestrationService) {
        this.paymentOrchestrationService = paymentOrchestrationService;
    }

    public PaymentLinkResponse generatePaymentLink(MerchantEntity merchant, PaymentLinkRequest request) {
        return paymentOrchestrationService.generatePaymentLink(merchant, request);
    }
}
