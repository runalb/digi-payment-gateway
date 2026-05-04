package com.digirestro.digi_payment_gateway.paymentchannel.dto;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;

public record PaymentLinkAdapterResponse(
        PaymentEntity payment) {}
