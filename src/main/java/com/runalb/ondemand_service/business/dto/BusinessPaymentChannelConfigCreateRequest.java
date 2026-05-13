package com.runalb.ondemand_service.business.dto;

import jakarta.validation.constraints.NotNull;

public record BusinessPaymentChannelConfigCreateRequest(
        // @NotNull Long paymentChannelId,
        // PaymentChannelNameEnum paymentChannelName,
        String configJson
) {}
