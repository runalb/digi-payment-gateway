package com.digirestro.digi_payment_gateway.payment_channel_strategy.impl;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.payment_channel_strategy.dto.PaymentLinkStrategyResponse;
import com.digirestro.digi_payment_gateway.payment_channel_strategy.interfaces.PaymentChannelStrategy;

public class PhonePePaymentChannelStrategy implements PaymentChannelStrategy {

    @Override
    public PaymentChannelNameEnum getChannelName() {
        return PaymentChannelNameEnum.PHONEPE;
    }

    @Override
    public PaymentLinkStrategyResponse createPaymentLink(PaymentEntity payment) {
        // TODO: Implement PhonePe payment link creation
        return null;
    }

   

    
}
