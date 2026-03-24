package com.digirestro.digi_payment_gateway.controller.webhooks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

@RestController
@RequestMapping("/api/v1/payment-channel-webhooks")
public class PaymentChannelWebhookController {

    @PostMapping("/dummy")
    public ResponseEntity<AdaptorWebhookResponse> receiveDummyWebhook(@RequestBody(required = false) String payload) {
        return ResponseEntity.ok(new AdaptorWebhookResponse(null, "DUMMY-REF", PaymentStatusEnum.SUCCESS, "DUMMY-TXN"));
    }
}
