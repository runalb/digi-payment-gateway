package com.digirestro.digi_payment_gateway.controller.webhooks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks/{channel}")
public class PaymentChannelWebhookController {

    @PostMapping("/dummy")
    public ResponseEntity<Void> receiveWebhook(@RequestBody(required = false) String payload) {
        return ResponseEntity.ok().build();
    }
}
