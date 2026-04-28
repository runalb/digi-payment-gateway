package com.runalb.ondemand_service.integration.channel.dto;

import com.runalb.ondemand_service.payment.enums.PaymentStatusEnum;

public record WebhookAdapterResponse(
        PaymentStatusEnum status,
        Long paymentId,
        String paymentChannelTxnId,
        String merchantReferencePaymentId
) {}
