package com.digirestro.digi_payment_gateway.adapter;

import com.digirestro.digi_payment_gateway.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.entity.MerchantChannelConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DummyPaymentChannelAdapter implements PaymentChannelAdapter {

    private final PaymentChannelEntity channel = new PaymentChannelEntity();

    public DummyPaymentChannelAdapter() {
        channel.setName(PaymentChannelNameEnum.DUMMY);
    }

    @Override
    public PaymentChannelEntity getChannel() {
        return channel;
    }

    @Override
    public AdapterPaymentLinkResponse createPaymentLink(PaymentLinkRequest request, MerchantConfigEntity merchantConfig, MerchantChannelConfigEntity channelConfig) {
        String txnId = "DUMMY-TXN-" + UUID.randomUUID();
        String paymentUrl = "https://dummy-pay.local/pay/" + txnId;
        return new AdapterPaymentLinkResponse(paymentUrl, txnId, PaymentStatusEnum.PENDING);
    }

    @Override
    public AdaptorWebhookResponse validateAndParseWebhook(String payload, String signature, String secret) {
        return new AdaptorWebhookResponse(null, "DUMMY-REF", PaymentStatusEnum.SUCCESS, "DUMMY-TXN");
    }
}
    