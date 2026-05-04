package com.digirestro.digi_payment_gateway.paymentchannel.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record WebhookAdapterResponse(
        PaymentStatusEnum status,
        Long paymentId,
        String paymentChannelTxnId,
        String merchantReferencePaymentId
) {}
