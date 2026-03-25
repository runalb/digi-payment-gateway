package com.digirestro.digi_payment_gateway.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import jakarta.validation.constraints.NotNull;

public record MerchantChannelConfigCreateRequest(
        @NotNull PaymentChannelNameEnum paymentChannelName,
        Boolean isActive,
        String configJson) {}
