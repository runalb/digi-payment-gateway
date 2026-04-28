package com.runalb.ondemand_service.integration.webhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.integration.webhook.entity.WebhookOutgoingMerchantLogEntity;

public interface WebhookOutgoingMerchantLogRepository extends JpaRepository<WebhookOutgoingMerchantLogEntity, Long> {
}
