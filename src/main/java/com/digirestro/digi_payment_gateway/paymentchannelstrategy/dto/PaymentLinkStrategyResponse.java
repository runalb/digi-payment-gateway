package com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;

public record PaymentLinkStrategyResponse(
        PaymentEntity payment
) {}
