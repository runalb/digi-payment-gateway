package com.runalb.ondemand_service.integration.webhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.integration.webhook.entity.WebhookIncomingPaymentChannelLogEntity;

public interface WebhookIncomingPaymentChannelLogRepository extends JpaRepository<WebhookIncomingPaymentChannelLogEntity, Long> {
}
