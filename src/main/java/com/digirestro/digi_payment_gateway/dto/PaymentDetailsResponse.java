package com.digirestro.digi_payment_gateway.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDetailsResponse(
        // payment details
        Long id,
        BigDecimal amount,
        String currency,
        PaymentStatusEnum status,

        // merchant details
        Long merchantId,
        String merchantReferencePaymentId,
        String merchantMetadataJson,

        // payment channel details
        Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        String paymentChannelTxnId,
        String paymentLinkUrl,

        // timestamps
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
