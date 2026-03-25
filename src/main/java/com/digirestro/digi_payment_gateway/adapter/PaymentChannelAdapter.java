package com.digirestro.digi_payment_gateway.adapter;

import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantChannelConfigEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;

public interface PaymentChannelAdapter {
    PaymentChannelEntity getChannel();

    AdapterPaymentLinkResponse createPaymentLink(PaymentEntity payment, MerchantConfigEntity merchantConfig, MerchantChannelConfigEntity channelConfig);

    AdaptorWebhookResponse validateAndParseWebhook(String payload, String signature, String secret);
}
