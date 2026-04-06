package com.digirestro.digi_payment_gateway.integration.dto.adaptor;

import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

public record AdaptorWebhookResponse(
        PaymentStatusEnum status,
        Long paymentId,
        String paymentChannelTxnId,
        String merchantReferencePaymentId
) {}
