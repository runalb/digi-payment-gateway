package com.digirestro.digi_payment_gateway.integration.api.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.service.PaymentService;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionIntegrationService {

    private final PaymentService paymentService;

    public TransactionIntegrationService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(Long paymentId, Long merchantId) {
        PaymentEntity payment = paymentService.findByIdAndMerchantId(paymentId, merchantId);
        return toPaymentDetailsResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailsResponse> listPaymentDetails(Long merchantId) {
        return paymentService.findAllByMerchantIdOrderByCreatedDateTimeDesc(merchantId)
                .stream()
                .map(this::toPaymentDetailsResponse)
                .toList();
    }

    private PaymentDetailsResponse toPaymentDetailsResponse(PaymentEntity payment) {
        return new PaymentDetailsResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getMerchant().getId(),
                payment.getMerchantReferencePaymentId(),
                payment.getMerchantMetadataJson(),
                payment.getPaymentChannel().getId(),
                payment.getPaymentChannel().getName(),
                payment.getPaymentChannelTxnId(),
                payment.getPaymentChannelPayLink(),
                payment.getCreatedDateTime(),
                payment.getUpdatedDateTime());
    }
}
