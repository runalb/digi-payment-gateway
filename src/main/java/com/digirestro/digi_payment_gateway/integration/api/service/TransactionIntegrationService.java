package com.digirestro.digi_payment_gateway.integration.api.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.integration.api.mapper.PaymentDetailsResponseMapper;
import com.digirestro.digi_payment_gateway.payment.service.PaymentService;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionIntegrationService {

    private final PaymentService paymentService;
    private final PaymentDetailsResponseMapper paymentDetailsResponseMapper;

    public TransactionIntegrationService(
            PaymentService paymentService, PaymentDetailsResponseMapper paymentDetailsResponseMapper) {
        this.paymentService = paymentService;
        this.paymentDetailsResponseMapper = paymentDetailsResponseMapper;
    }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(Long paymentId, Long merchantId) {
        return paymentDetailsResponseMapper.toResponse(
                paymentService.findByIdAndMerchantId(paymentId, merchantId));
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailsResponse> listPaymentDetails(Long merchantId) {
        return paymentDetailsResponseMapper.toResponseList(
                paymentService.findAllByMerchantIdOrderByCreatedDateTimeDesc(merchantId));
    }
}
