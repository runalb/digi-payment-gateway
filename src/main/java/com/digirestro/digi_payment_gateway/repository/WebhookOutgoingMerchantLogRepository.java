package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.WebhookOutgoingMerchantLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookOutgoingMerchantLogRepository extends JpaRepository<WebhookOutgoingMerchantLogEntity, Long> {
}
