package com.digirestro.digi_payment_gateway.integration.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

public record PaymentLinkResponse(
        Long paymentId,
        String paymentLinkUrl,
        String paymentChannelTxnId,
        PaymentStatusEnum status) {}
