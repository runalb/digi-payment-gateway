package com.digirestro.digi_payment_gateway.integration.api.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.CheckoutRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.CheckoutResponse;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentOriginEnum;
import com.digirestro.digi_payment_gateway.payment.service.PaymentOrchestrationService;

import org.springframework.stereotype.Service;

@Service
public class CheckoutIntegrationService {

    private final PaymentOrchestrationService paymentOrchestrationService;

    public CheckoutIntegrationService(PaymentOrchestrationService paymentOrchestrationService) {
        this.paymentOrchestrationService = paymentOrchestrationService;
    }

    public CheckoutResponse generateCheckout(MerchantEntity merchant, CheckoutRequest request) {
        return paymentOrchestrationService.generateCheckout(merchant, request, PaymentOriginEnum.INTEGRATION);
    }
}
