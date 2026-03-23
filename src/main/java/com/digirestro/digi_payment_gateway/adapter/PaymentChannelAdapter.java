package com.digirestro.digi_payment_gateway.adapter;

import com.digirestro.digi_payment_gateway.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.PaymentStatusResponse;
import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantChannelConfigEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;

public interface PaymentChannelAdapter {
    PaymentChannelEntity getChannel();

    PaymentLinkResponse createPaymentLink(PaymentLinkRequest request, MerchantConfigEntity merchantConfig, MerchantChannelConfigEntity channelConfig);

    PaymentStatusResponse validateAndParseWebhook(String payload, String signature, String secret);
}
