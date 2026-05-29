package com.digirestro.digi_payment_gateway.payment_channel.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record CheckoutStrategyResponse(
        String paymentChannelCheckoutUrl,
        String paymentChannelTxnId,
        PaymentStatusEnum status) {}
