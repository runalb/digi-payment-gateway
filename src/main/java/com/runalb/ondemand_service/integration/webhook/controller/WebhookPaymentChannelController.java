package com.runalb.ondemand_service.integration.webhook.controller;

import com.runalb.ondemand_service.integration.channel.adapter.PaymentChannelAdapter;
import com.runalb.ondemand_service.integration.channel.dto.WebhookAdapterResponse;
import com.runalb.ondemand_service.integration.channel.service.PaymentChannelAdapterResolver;
import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;

import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/webhook/v1/payment-channel-webhooks")
public class WebhookPaymentChannelController {

    private final PaymentChannelAdapterResolver adapterResolver;

    public WebhookPaymentChannelController(PaymentChannelAdapterResolver adapterResolver) {
        this.adapterResolver = adapterResolver;
    }

    /** 
     * Receives channel-specific webhooks at /webhook/v1/payment-channel-webhooks/{@code {channelKey}}.
     * <ul> Example URLs:
     * <li> baseUrl/webhook/v1/payment-channel-webhooks/test</li>
     * <li> baseUrl/webhook/v1/payment-channel-webhooks/stripe</li>
     * <li> baseUrl/webhook/v1/payment-channel-webhooks/razorpay</li>
     * <li> baseUrl/webhook/v1/payment-channel-webhooks/paymob</li>
     * <li> baseUrl/webhook/v1/payment-channel-webhooks/xplorpay</li>
     * </ul>
     * @param channelKey The name of the payment channel (case-insensitive enum name)
     * @param webhookPayload The payload of the webhook
     * @return The response of the webhook
     */
    @PostMapping("/{channelKey}")
    public ResponseEntity<WebhookAdapterResponse> receiveWebhook(
        @PathVariable String channelKey, @RequestBody Map<String, Object> webhookPayload) {
        log.info("Received {} webhook: {}", channelKey, webhookPayload);

        PaymentChannelNameEnum channelName = parseChannelKey(channelKey);
        log.info("Parsed channel name: {}", channelName);

        PaymentChannelAdapter adapter = adapterResolver.requireByChannelName(channelName);

        WebhookAdapterResponse response = adapter.validateAndParseWebhook(webhookPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private static PaymentChannelNameEnum parseChannelKey(String channelKey) {
        try {
            return PaymentChannelNameEnum.valueOf(channelKey.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown payment channel: " + channelKey);
        }
    }
}
