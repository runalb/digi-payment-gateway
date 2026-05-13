package com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record WebhookStrategyResponse(
        PaymentStatusEnum status,
        Long paymentId,
        String paymentChannelTxnId,
        String merchantReferencePaymentId
) {}
