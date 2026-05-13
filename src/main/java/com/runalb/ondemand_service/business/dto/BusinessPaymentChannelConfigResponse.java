package com.runalb.ondemand_service.business.dto;

// import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;

public record BusinessPaymentChannelConfigResponse(
        Long id,
        Long businessId,
        // Long paymentChannelId,
        // PaymentChannelNameEnum paymentChannelName,
        String configJson
) {}
