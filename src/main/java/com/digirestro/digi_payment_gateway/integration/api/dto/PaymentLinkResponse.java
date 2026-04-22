package com.digirestro.digi_payment_gateway.integration.api.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record PaymentLinkResponse(
        Long paymentId,
        // String digiPaymentLink,
        String paymentChannelPayLink,
        String paymentChannelTxnId,
        PaymentStatusEnum status) {}
