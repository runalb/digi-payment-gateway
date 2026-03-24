package com.digirestro.digi_payment_gateway.dto.adaptor;

import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

public record AdaptorWebhookResponse(
        Long paymentId,
        String merchantReferencePaymentId,
        PaymentStatusEnum status,
        String paymentChannelTxnId) {}
