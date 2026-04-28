package com.runalb.ondemand_service.integration.channel.adapter;

import com.runalb.ondemand_service.integration.channel.dto.PaymentLinkAdapterResponse;
import com.runalb.ondemand_service.integration.channel.dto.WebhookAdapterResponse;
import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
import com.runalb.ondemand_service.payment.entity.PaymentEntity;
import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;
import com.runalb.ondemand_service.payment.enums.PaymentStatusEnum;
import com.runalb.ondemand_service.payment.service.PaymentChannelService;
import com.runalb.ondemand_service.payment.service.PaymentService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public PaymentLinkAdapterResponse createPaymentLink(PaymentEntity payment) {
        String paymentChannelTxnId = "TEST-TXN-" + UUID.randomUUID();
        String amountParam = URLEncoder.encode(payment.getAmount().toPlainString(), StandardCharsets.UTF_8);
        String currencyParam = URLEncoder.encode(payment.getCurrency(), StandardCharsets.UTF_8);
        String paymentUrl = "http://localhost:8080/test-payment-link.html?paymentId=" + payment.getId()
                + "&merchantId=" + payment.getMerchant().getId()
                + "&amount=" + amountParam
                + "&currency=" + currencyParam;

        if (!paymentChannelTxnId.isEmpty() && !paymentUrl.isEmpty()) {
            payment.setPaymentChannelTxnId(paymentChannelTxnId);
            payment.setPaymentChannelPayLink(paymentUrl);
            payment.setStatus(PaymentStatusEnum.PAYMENT_LINK_GENERATED);
        }

        return new PaymentLinkAdapterResponse(payment);
    }

    @Override
    @Transactional
    public WebhookAdapterResponse validateAndParseWebhook(Map<String, Object> webhookPayload) {
        log.info("Validating and parsing webhook payload: {}", webhookPayload);

        PaymentStatusEnum paymentStatus = PaymentStatusEnum.valueOf((String) webhookPayload.get("paymentStatus"));
        Long paymentId = (Long) webhookPayload.get("paymentId");
        if (paymentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required in webhook payload");
        }

        PaymentEntity payment = paymentService.findById(paymentId);
        payment.setStatus(paymentStatus);

        payment = paymentService.save(payment);

        return new WebhookAdapterResponse(
                payment.getStatus(),
                payment.getId(),
                payment.getPaymentChannelTxnId(),
                payment.getMerchantReferencePaymentId());
    }
}
