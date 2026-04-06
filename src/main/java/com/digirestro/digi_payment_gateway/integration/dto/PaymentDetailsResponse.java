package com.digirestro.digi_payment_gateway.integration.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        String paymentLinkUrl,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
