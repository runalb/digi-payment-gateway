package com.digirestro.digi_payment_gateway.integration.webhooks;

import com.digirestro.digi_payment_gateway.integration.adapter.TestPaymentChannelAdapter;
import com.digirestro.digi_payment_gateway.integration.dto.adaptor.AdaptorWebhookResponse;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/webhook/v1/payment-channel-webhooks")
public class PaymentChannelWebhookController {
    private final TestPaymentChannelAdapter testPaymentChannelAdapter;

    public PaymentChannelWebhookController(TestPaymentChannelAdapter testPaymentChannelAdapter) {
        this.testPaymentChannelAdapter = testPaymentChannelAdapter;
    }

    @PostMapping(value = "/test")
    public ResponseEntity<AdaptorWebhookResponse> receiveTestWebhook(@RequestBody Map<String, Object> webhookPayload) {
        log.info("Received test webhook: {}", webhookPayload);
        AdaptorWebhookResponse response = testPaymentChannelAdapter.validateAndParseWebhook(webhookPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
