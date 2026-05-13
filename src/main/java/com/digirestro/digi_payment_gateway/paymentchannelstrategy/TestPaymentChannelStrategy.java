package com.digirestro.digi_payment_gateway.paymentchannelstrategy;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.payment.service.PaymentService;
import com.digirestro.digi_payment_gateway.paymentchannel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.PaymentLinkStrategyResponse;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.WebhookStrategyResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class TestPaymentChannelStrategy implements PaymentChannelStrategy {

    private static final PaymentChannelNameEnum CHANNEL_NAME = PaymentChannelNameEnum.TEST;
    private final PaymentService paymentService;
    private final String testPaymentLinkBaseUrl;

    public TestPaymentChannelStrategy(
            PaymentService paymentService,
            @Value("${payment-channel.test.payment-link-base-url:http://localhost:8080/test-payment-link.html}")
            String testPaymentLinkBaseUrl) {
        this.paymentService = paymentService;
        this.testPaymentLinkBaseUrl = testPaymentLinkBaseUrl;
    }

    @Override
    public PaymentChannelNameEnum getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    public PaymentLinkStrategyResponse createPaymentLink(PaymentEntity payment) {
        String paymentChannelTxnId = "TEST-TXN-" + UUID.randomUUID();
        String amountParam = URLEncoder.encode(payment.getAmount().toPlainString(), StandardCharsets.UTF_8);
        String currencyParam = URLEncoder.encode(payment.getCurrency(), StandardCharsets.UTF_8);
        String paymentUrl = testPaymentLinkBaseUrl + "?paymentId=" + payment.getId()
                + "&merchantId=" + payment.getMerchant().getId()
                + "&amount=" + amountParam
                + "&currency=" + currencyParam;

        if (!paymentChannelTxnId.isEmpty() && !paymentUrl.isEmpty()) {
            payment.setPaymentChannelTxnId(paymentChannelTxnId);
            payment.setPaymentChannelPayLink(paymentUrl);
            payment.setStatus(PaymentStatusEnum.PAYMENT_LINK_GENERATED);
        }

        return new PaymentLinkStrategyResponse(payment);
    }

    @Override
    @Transactional
    public WebhookStrategyResponse validateAndParseWebhook(Map<String, Object> webhookPayload) {
        log.info("Validating and parsing webhook payload: {}", webhookPayload);

        PaymentStatusEnum paymentStatus = PaymentStatusEnum.valueOf((String) webhookPayload.get("paymentStatus"));
        Long paymentId = (Long) webhookPayload.get("paymentId");
        if (paymentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required in webhook payload");
        }

        PaymentEntity payment = paymentService.findById(paymentId);
        payment.setStatus(paymentStatus);

        payment = paymentService.save(payment);

        return new WebhookStrategyResponse(
                payment.getStatus(),
                payment.getId(),
                payment.getPaymentChannelTxnId(),
                payment.getMerchantReferencePaymentId());
    }
}
