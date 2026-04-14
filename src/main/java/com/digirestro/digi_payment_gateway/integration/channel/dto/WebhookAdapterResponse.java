package com.digirestro.digi_payment_gateway.integration.channel.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

public record WebhookAdapterResponse(
        PaymentStatusEnum status,
        Long paymentId,
        String paymentChannelTxnId,
        String merchantReferencePaymentId
) {}
