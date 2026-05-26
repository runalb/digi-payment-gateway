package com.digirestro.digi_payment_gateway.merchant_webhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.merchant_webhook.entity.MerchantWebhookEntity;


public interface MerchantWebhookRepository extends JpaRepository<MerchantWebhookEntity, Long> {
}
