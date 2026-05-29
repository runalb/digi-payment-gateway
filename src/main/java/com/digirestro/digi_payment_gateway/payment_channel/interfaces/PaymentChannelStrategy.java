package com.digirestro.digi_payment_gateway.payment_channel.interfaces;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment_channel.dto.CheckoutStrategyResponse;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;

public interface PaymentChannelStrategy {

    PaymentChannelNameEnum getChannelName();

    /**
     * Returns channel checkout data for an already-persisted payment.
     * Must not save the payment or set its status — {@code PaymentOrchestrationService} owns persistence.
     */
    CheckoutStrategyResponse createCheckout(PaymentEntity payment);




    /** 
     * TODO: Webhook flow not fully implemented yet. Remove this method once webhook flow is fully implemented. This method is only for initial testing purposes.
     *
     * <p>Validates and parses a channel webhook payload.
     * <p>Must not load or save the payment — {@code PaymentChannelWebhookOrchestrationService} owns persistence.
     */
    // WebhookStrategyResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
