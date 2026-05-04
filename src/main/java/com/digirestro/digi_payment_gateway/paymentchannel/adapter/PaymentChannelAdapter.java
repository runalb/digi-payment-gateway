package com.digirestro.digi_payment_gateway.paymentchannel.adapter;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.paymentchannel.dto.PaymentLinkAdapterResponse;
import com.digirestro.digi_payment_gateway.paymentchannel.dto.WebhookAdapterResponse;

import java.util.Map;

public interface PaymentChannelAdapter {
    PaymentChannelEntity getChannel();

    PaymentLinkAdapterResponse createPaymentLink(PaymentEntity payment);

    WebhookAdapterResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
