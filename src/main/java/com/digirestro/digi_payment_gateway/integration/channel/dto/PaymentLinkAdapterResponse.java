package com.digirestro.digi_payment_gateway.integration.channel.dto;

import com.digirestro.digi_payment_gateway.entity.PaymentEntity;

public record PaymentLinkAdapterResponse(
        PaymentEntity payment) {}
