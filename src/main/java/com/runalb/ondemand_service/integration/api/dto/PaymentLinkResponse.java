package com.runalb.ondemand_service.integration.api.dto;

import com.runalb.ondemand_service.payment.enums.PaymentStatusEnum;

public record PaymentLinkResponse(
        Long paymentId,
        // String digiPaymentLink,
        String paymentChannelPayLink,
        String paymentChannelTxnId,
        PaymentStatusEnum status) {}
