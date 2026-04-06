package com.digirestro.digi_payment_gateway.integration.adapter;

import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.integration.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.integration.dto.adaptor.AdaptorWebhookResponse;
import java.util.Map;

public interface PaymentChannelAdapter {
    PaymentChannelEntity getChannel();

    AdapterPaymentLinkResponse createPaymentLink(PaymentEntity payment);

    AdaptorWebhookResponse validateAndParseWebhook(Map<String, Object> webhookPayload);
}
