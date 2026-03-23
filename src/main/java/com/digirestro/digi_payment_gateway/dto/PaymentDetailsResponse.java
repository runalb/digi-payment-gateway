package com.digirestro.digi_payment_gateway.dto;

import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDetailsResponse(
        Long id,
        Long merchantId,
        Long channelConfigId,
        Long paymentChannelId,
        PaymentChannelNameEnum paymentChannelName,
        String merchantReferencePaymentId,
        String paymentChannelTxnId,
        BigDecimal amount,
        String currency,
        PaymentStatusEnum status,
        String paymentLinkUrl,
        String merchantMetadataJson,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
