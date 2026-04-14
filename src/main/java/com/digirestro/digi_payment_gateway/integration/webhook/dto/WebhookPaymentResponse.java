package com.digirestro.digi_payment_gateway.integration.webhook.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record WebhookPaymentResponse(
        Long paymentId,
        String merchantReferencePaymentId,
        PaymentStatusEnum status,
        String paymentChannelTxnId) {}
