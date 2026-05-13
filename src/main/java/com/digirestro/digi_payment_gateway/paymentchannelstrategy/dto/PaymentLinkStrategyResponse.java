package com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto;

public record PaymentLinkStrategyResponse(
        String paymentChannelPayLink,
        String paymentChannelTxnId
) {}
