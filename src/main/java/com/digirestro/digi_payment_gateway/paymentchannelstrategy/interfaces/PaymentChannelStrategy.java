package com.digirestro.digi_payment_gateway.paymentchannelstrategy.interfaces;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.paymentchannel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.PaymentLinkStrategyResponse;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.WebhookStrategyResponse;

import java.util.Map;

public interface PaymentChannelStrategy {

    PaymentChannelNameEnum getChannelName();

    /**
     * Returns channel link data for an already-persisted payment.
     * Must not save the payment or set its status — {@code PaymentOrchestrationService} owns persistence.
     */
    PaymentLinkStrategyResponse createPaymentLink(PaymentEntity payment);




    // TODO: Webhook flow not fully implemented yet.
    /**
     * Validates and parses a channel webhook payload.
     * Must not load or save the payment — {@code PaymentChannelWebhookOrchestrationService} owns persistence.
     */
    WebhookStrategyResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
