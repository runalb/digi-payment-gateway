package com.runalb.ondemand_service.integration.webhook.dto;

import com.runalb.ondemand_service.payment.enums.PaymentStatusEnum;

public record WebhookPaymentResponse(
        Long paymentId,
        String merchantReferencePaymentId,
        PaymentStatusEnum status,
        String paymentChannelTxnId) {}
