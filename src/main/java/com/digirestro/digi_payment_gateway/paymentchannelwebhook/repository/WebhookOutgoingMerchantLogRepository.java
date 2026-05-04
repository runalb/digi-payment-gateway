package com.digirestro.digi_payment_gateway.paymentchannelwebhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.paymentchannelwebhook.entity.WebhookOutgoingMerchantLogEntity;

public interface WebhookOutgoingMerchantLogRepository extends JpaRepository<WebhookOutgoingMerchantLogEntity, Long> {
}
