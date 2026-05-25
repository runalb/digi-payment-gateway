package com.digirestro.digi_payment_gateway.paymentchannelstrategy.impl;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.paymentchannel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.interfaces.PaymentChannelStrategy;
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
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class TestPaymentChannelStrategy implements PaymentChannelStrategy {

    private static final PaymentChannelNameEnum CHANNEL_NAME = PaymentChannelNameEnum.TEST;
    private final String testPaymentLinkBaseUrl;

    public TestPaymentChannelStrategy(
            @Value("${payment-channel.test.payment-link-base-url:http://localhost:8080/test-payment-link.html}")
            String testPaymentLinkBaseUrl) {
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

        paymentUrl = "https://gateway-int.clearent.net/paylink/N32rg0Z40t9";

        return new PaymentLinkStrategyResponse(
                paymentUrl,
                paymentChannelTxnId,
                PaymentStatusEnum.PAYMENT_LINK_GENERATED);
    }

    // @Override
    // public WebhookStrategyResponse validateAndParseWebhook(Map<String, Object> webhookPayload) {
    //     log.info("Validating and parsing webhook payload: {}", webhookPayload);

    //     PaymentStatusEnum paymentStatus = PaymentStatusEnum.valueOf((String) webhookPayload.get("paymentStatus"));
    //     Long paymentId = extractPaymentId(webhookPayload.get("paymentId"));
    //     if (paymentId == null) {
    //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required in webhook payload");
    //     }

    //     return new WebhookStrategyResponse(paymentStatus, paymentId, null, null);
    // }

    private static Long extractPaymentId(Object paymentIdValue) {
        if (paymentIdValue == null) {
            return null;
        }
        if (paymentIdValue instanceof Number number) {
            return number.longValue();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID must be a number");
    }
}
