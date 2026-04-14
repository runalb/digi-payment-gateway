package com.digirestro.digi_payment_gateway.integration.channel.adapter;

import com.digirestro.digi_payment_gateway.integration.channel.dto.PaymentLinkAdapterResponse;
import com.digirestro.digi_payment_gateway.integration.channel.dto.WebhookAdapterResponse;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;

import java.util.Map;

public interface PaymentChannelAdapter {
    PaymentChannelEntity getChannel();

    PaymentLinkAdapterResponse createPaymentLink(PaymentEntity payment);

    WebhookAdapterResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
