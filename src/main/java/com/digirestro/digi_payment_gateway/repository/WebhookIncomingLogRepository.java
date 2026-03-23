package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.WebhookIncomingLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookIncomingLogRepository extends JpaRepository<WebhookIncomingLogEntity, Long> {
}
