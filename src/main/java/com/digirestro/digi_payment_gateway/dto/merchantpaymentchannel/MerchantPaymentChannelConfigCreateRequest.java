package com.digirestro.digi_payment_gateway.dto.merchantpaymentchannel;

import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;

import jakarta.validation.constraints.NotNull;

public record MerchantPaymentChannelConfigCreateRequest(
        @NotNull Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        String configJson) {}
