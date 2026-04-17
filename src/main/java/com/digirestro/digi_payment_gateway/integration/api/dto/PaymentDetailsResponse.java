package com.digirestro.digi_payment_gateway.integration.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record PaymentDetailsResponse(
        Long id,
        BigDecimal amount,
        String currency,
        PaymentStatusEnum status,
        Long merchantId,
        String merchantReferencePaymentId,
        String merchantMetadataJson,
        Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        String paymentChannelTxnId,
        String digiPaymentLink,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
