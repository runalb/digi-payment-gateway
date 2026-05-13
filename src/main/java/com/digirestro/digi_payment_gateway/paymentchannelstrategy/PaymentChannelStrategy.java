package com.digirestro.digi_payment_gateway.paymentchannelstrategy;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.paymentchannel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.PaymentLinkStrategyResponse;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.WebhookStrategyResponse;

import java.util.Map;

public interface PaymentChannelStrategy {

    PaymentChannelNameEnum getChannelName();

    PaymentLinkStrategyResponse createPaymentLink(PaymentEntity payment);

    WebhookStrategyResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
