package com.digirestro.digi_payment_gateway.payment_channel_webhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.payment_channel_webhook.entity.PaymentChannelWebhookEntity;

public interface PaymentChannelWebhookRepository extends JpaRepository<PaymentChannelWebhookEntity, Long> {
}
