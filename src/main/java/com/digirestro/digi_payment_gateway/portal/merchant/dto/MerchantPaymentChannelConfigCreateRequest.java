package com.digirestro.digi_payment_gateway.portal.merchant.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentChannelNameEnum;

import jakarta.validation.constraints.NotNull;

public record MerchantPaymentChannelConfigCreateRequest(
        @NotNull Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        String configJson) {}
