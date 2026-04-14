package com.digirestro.digi_payment_gateway.integration.channel.adapter;

import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.integration.channel.dto.PaymentLinkAdapterResponse;
import com.digirestro.digi_payment_gateway.integration.channel.dto.WebhookAdapterResponse;

import java.util.Map;

public interface PaymentChannelAdapter {
    PaymentChannelEntity getChannel();

    PaymentLinkAdapterResponse createPaymentLink(PaymentEntity payment);

    WebhookAdapterResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
