package com.digirestro.digi_payment_gateway.merchantwebhook.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record MerchantWebhookResponse(
        Long paymentId,
        String merchantReferencePaymentId,
        PaymentStatusEnum status,
        String paymentChannelTxnId) {}
