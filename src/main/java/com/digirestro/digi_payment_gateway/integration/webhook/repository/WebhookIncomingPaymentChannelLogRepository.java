package com.digirestro.digi_payment_gateway.integration.webhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.integration.webhook.entity.WebhookIncomingPaymentChannelLogEntity;

public interface WebhookIncomingPaymentChannelLogRepository extends JpaRepository<WebhookIncomingPaymentChannelLogEntity, Long> {
}
