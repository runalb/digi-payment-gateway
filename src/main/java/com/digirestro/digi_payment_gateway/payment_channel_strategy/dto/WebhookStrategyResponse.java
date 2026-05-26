package com.digirestro.digi_payment_gateway.payment_channel_strategy.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record WebhookStrategyResponse(
        PaymentStatusEnum status,
        Long paymentId,
        String paymentChannelTxnId,
        String merchantReferencePaymentId
) {}
