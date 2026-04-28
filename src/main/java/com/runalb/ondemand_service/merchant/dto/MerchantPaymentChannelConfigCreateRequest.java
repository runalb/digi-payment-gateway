package com.runalb.ondemand_service.merchant.dto;

import jakarta.validation.constraints.NotNull;

public record MerchantPaymentChannelConfigCreateRequest(
        // @NotNull Long paymentChannelId,
        // PaymentChannelNameEnum paymentChannelName,
        String configJson) {}
