package com.runalb.ondemand_service.integration.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;
import com.runalb.ondemand_service.payment.enums.PaymentStatusEnum;

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
        // String digiPaymentLink,
        String paymentChannelPayLink,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
