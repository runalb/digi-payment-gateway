package com.digirestro.digi_payment_gateway.integration.webhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.integration.webhook.entity.WebhookOutgoingMerchantLogEntity;

public interface WebhookOutgoingMerchantLogRepository extends JpaRepository<WebhookOutgoingMerchantLogEntity, Long> {
}
