package com.runalb.ondemand_service.integration.channel.adapter;

import com.runalb.ondemand_service.integration.channel.dto.PaymentLinkAdapterResponse;
import com.runalb.ondemand_service.integration.channel.dto.WebhookAdapterResponse;
import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
import com.runalb.ondemand_service.payment.entity.PaymentEntity;

import java.util.Map;

public interface PaymentChannelAdapter {
    PaymentChannelEntity getChannel();

    PaymentLinkAdapterResponse createPaymentLink(PaymentEntity payment);

    WebhookAdapterResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
