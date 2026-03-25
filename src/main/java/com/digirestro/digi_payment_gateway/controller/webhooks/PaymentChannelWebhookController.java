package com.digirestro.digi_payment_gateway.controller.webhooks;

import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

import java.util.Map;

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

    @PostMapping(value = "/test")
    public ResponseEntity<AdaptorWebhookResponse> receiveTestWebhook(@RequestBody Map<String, Object> body) {
        log.info("Received test webhook: {}", body);
        return ResponseEntity.ok(new AdaptorWebhookResponse(PaymentStatusEnum.SUCCESS, null, null, null));
    }


}
