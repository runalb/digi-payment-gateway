package com.digirestro.digi_payment_gateway.adapter;

import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.repository.PaymentChannelRepository;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestPaymentChannelAdapter implements PaymentChannelAdapter {

    private final PaymentChannelEntity channel;

    public TestPaymentChannelAdapter(PaymentChannelRepository paymentChannelRepository) {
        this.channel = paymentChannelRepository
                .findByName(PaymentChannelNameEnum.TEST)
                .orElseThrow(() -> new IllegalStateException("Payment channel TEST not found in database"));
    }

    @Override
    public PaymentChannelEntity getChannel() {
        return channel;
    }

    @Override
    public AdapterPaymentLinkResponse createPaymentLink(PaymentEntity payment) {
        String paymentChannelTxnId = "TEST-TXN-" + UUID.randomUUID();
        String paymentUrl = "http://localhost:8080/test-payment-link.html?paymentId=" + payment.getId() + "&merchantId=" + payment.getMerchant().getId();
        
        if (!paymentChannelTxnId.isEmpty() && !paymentUrl.isEmpty()) {
            payment.setPaymentChannelTxnId(paymentChannelTxnId);
            payment.setPaymentLinkUrl(paymentUrl);
            payment.setStatus(PaymentStatusEnum.PAYMENT_LINK_GENERATED);
        }

        return new AdapterPaymentLinkResponse(payment);
    }

    @Override
    public AdaptorWebhookResponse validateAndParseWebhook(Map<String, Object> webhookPayload) {
        log.info("Validating and parsing webhook payload: {}", webhookPayload);
        return new AdaptorWebhookResponse(PaymentStatusEnum.SUCCESS, null, null, null);
    }
}
