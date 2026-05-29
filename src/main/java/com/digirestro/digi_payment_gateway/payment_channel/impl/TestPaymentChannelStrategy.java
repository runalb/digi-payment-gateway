package com.digirestro.digi_payment_gateway.payment_channel.impl;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.payment_channel.dto.CheckoutStrategyResponse;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.payment_channel.interfaces.PaymentChannelStrategy;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestPaymentChannelStrategy implements PaymentChannelStrategy {

    private static final String TEST_CHECKOUT_BASE_URL = "http://localhost:8080/test-checkout.html";

    @Override
    public PaymentChannelNameEnum getChannelName() {
        return PaymentChannelNameEnum.TEST;
    }

    @Override
    public CheckoutStrategyResponse createCheckout(PaymentEntity payment) {
        String paymentChannelTxnId = "TEST-TXN-" + UUID.randomUUID();
        String amountParam = URLEncoder.encode(payment.getAmount().toPlainString(), StandardCharsets.UTF_8);
        String currencyParam = URLEncoder.encode(payment.getCurrency(), StandardCharsets.UTF_8);
        String checkoutUrl = TEST_CHECKOUT_BASE_URL + "?paymentId=" + payment.getId()
                + "&merchantId=" + payment.getMerchant().getId()
                + "&amount=" + amountParam
                + "&currency=" + currencyParam;

        return new CheckoutStrategyResponse(
                checkoutUrl, paymentChannelTxnId, PaymentStatusEnum.CHECKOUT_URL_GENERATED);
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

    // private static Long extractPaymentId(Object paymentIdValue) {
    //     if (paymentIdValue == null) {
    //         return null;
    //     }
    //     if (paymentIdValue instanceof Number number) {
    //         return number.longValue();
    //     }
    //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID must be a number");
    // }
}
