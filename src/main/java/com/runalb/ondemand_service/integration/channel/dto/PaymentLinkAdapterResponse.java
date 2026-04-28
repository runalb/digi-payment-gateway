package com.runalb.ondemand_service.integration.channel.dto;

import com.runalb.ondemand_service.payment.entity.PaymentEntity;

public record PaymentLinkAdapterResponse(
        PaymentEntity payment) {}
