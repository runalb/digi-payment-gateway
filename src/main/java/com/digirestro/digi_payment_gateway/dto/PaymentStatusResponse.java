package com.digirestro.digi_payment_gateway.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

/** Result of parsing/validating a payment-channel webhook (adapter layer). */
public record PaymentStatusResponse(
        Long paymentId,
        String merchantReferencePaymentId,
        PaymentStatusEnum status,
        String paymentChannelTxnId) {}
