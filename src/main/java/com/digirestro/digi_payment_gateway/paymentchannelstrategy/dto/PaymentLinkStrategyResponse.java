package com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto;

import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;

public record PaymentLinkStrategyResponse(
        String paymentChannelPayLink,
        String paymentChannelTxnId,
        PaymentStatusEnum status
) {}
