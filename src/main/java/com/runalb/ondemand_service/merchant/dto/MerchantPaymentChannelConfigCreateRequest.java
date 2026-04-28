package com.runalb.ondemand_service.merchant.dto;

import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;

import jakarta.validation.constraints.NotNull;

public record MerchantPaymentChannelConfigCreateRequest(
        @NotNull Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        String configJson) {}
