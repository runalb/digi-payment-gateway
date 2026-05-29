package com.digirestro.digi_payment_gateway.integration.api.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record CheckoutResponse(
        Long paymentId,
        // String digiPaymentLink,
        String paymentChannelCheckoutUrl,
        String paymentChannelTxnId,
        PaymentStatusEnum status) {}
