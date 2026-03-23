package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.WebhookMerchantLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookMerchantLogRepository extends JpaRepository<WebhookMerchantLogEntity, Long> {
}
