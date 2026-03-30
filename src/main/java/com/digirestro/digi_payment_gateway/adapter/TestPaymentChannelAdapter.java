package com.digirestro.digi_payment_gateway.adapter;

import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.entity.MerchantChannelConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestPaymentChannelAdapter implements PaymentChannelAdapter {

    private final PaymentChannelEntity channel = new PaymentChannelEntity();

    public TestPaymentChannelAdapter() {
        channel.setName(PaymentChannelNameEnum.TEST);
    }

    @Override
    public PaymentChannelEntity getChannel() {
        return channel;
    }

    @Override
    public AdapterPaymentLinkResponse createPaymentLink(PaymentEntity payment, MerchantConfigEntity merchantConfig, MerchantChannelConfigEntity channelConfig) {
        String paymentChannelTxnId = "TEST-TXN-" + UUID.randomUUID();
        String paymentUrl = "http://localhost:8080/test-payment-link.html?paymentId=" + payment.getId()
                + "&merchantId=" + payment.getMerchant().getId();
        return new AdapterPaymentLinkResponse(paymentUrl, paymentChannelTxnId, PaymentStatusEnum.PENDING);
    }

    @Override
    public AdaptorWebhookResponse validateAndParseWebhook(Map<String, Object> webhookPayload) {
        log.info("Validating and parsing webhook payload: {}", webhookPayload);
        return new AdaptorWebhookResponse(PaymentStatusEnum.SUCCESS, null, null, null);
    }
}
