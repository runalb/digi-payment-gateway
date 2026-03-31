package com.digirestro.digi_payment_gateway.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;

public record MerchantPaymentChannelConfigResponse(
        Long id,
        Long merchantId,
        Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        Boolean isActive,
        String configJson) {}
