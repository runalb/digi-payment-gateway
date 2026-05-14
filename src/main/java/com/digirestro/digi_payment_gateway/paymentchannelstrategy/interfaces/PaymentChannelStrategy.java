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




    /** 
     * TODO: Webhook flow not fully implemented yet. Remove this method once webhook flow is fully implemented. This method is only for initial testing purposes.
     *
     * <p>Validates and parses a channel webhook payload.
     * <p>Must not load or save the payment — {@code PaymentChannelWebhookOrchestrationService} owns persistence.
     */
    // WebhookStrategyResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
