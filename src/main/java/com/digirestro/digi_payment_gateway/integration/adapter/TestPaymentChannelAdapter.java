package com.digirestro.digi_payment_gateway.integration.adapter;

import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.integration.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.integration.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.service.PaymentChannelService;
import com.digirestro.digi_payment_gateway.service.PaymentService;

import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class TestPaymentChannelAdapter implements PaymentChannelAdapter {

    private final PaymentChannelEntity channel;
    private final PaymentService paymentService;

    public TestPaymentChannelAdapter(PaymentChannelService paymentChannelService, PaymentService paymentService) {
        this.channel = paymentChannelService.findByName(PaymentChannelNameEnum.TEST);
        this.paymentService = paymentService;
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
    @Transactional
    public AdaptorWebhookResponse validateAndParseWebhook(Map<String, Object> webhookPayload) {
        log.info("Validating and parsing webhook payload: {}", webhookPayload);

        var paymentStatus = PaymentStatusEnum.valueOf((String) webhookPayload.get("paymentStatus"));
        var paymentIdValue = (Number) webhookPayload.get("paymentId");
        if (paymentIdValue == null) {
            throw new IllegalArgumentException("paymentId is required");
        }
        var paymentId = paymentIdValue.longValue();

        var payment = paymentService.findById(paymentId);
        payment.setStatus(paymentStatus);

        var updatedPayment = paymentService.save(payment);

        return new AdaptorWebhookResponse(
                updatedPayment.getStatus(),
                updatedPayment.getId(),
                updatedPayment.getPaymentChannelTxnId(),
                updatedPayment.getMerchantReferencePaymentId());
    }
}
