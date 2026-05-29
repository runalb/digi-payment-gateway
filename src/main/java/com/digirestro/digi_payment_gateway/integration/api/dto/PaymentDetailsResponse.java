package com.digirestro.digi_payment_gateway.integration.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;

public record PaymentDetailsResponse(
        Long id,
        BigDecimal amount,
        String currency,
        PaymentStatusEnum status,
        Long merchantId,
        String merchantReferencePaymentId,
        Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        String paymentChannelTxnId,
        String paymentChannelCheckoutUrl,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
