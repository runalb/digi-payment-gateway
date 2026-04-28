package com.runalb.ondemand_service.merchant.dto;

import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;

public record MerchantPaymentChannelConfigResponse(
        Long id,
        Long merchantId,
        Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        Boolean isActive,
        String configJson) {}
