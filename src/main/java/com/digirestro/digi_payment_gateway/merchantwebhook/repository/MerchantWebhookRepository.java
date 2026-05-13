package com.digirestro.digi_payment_gateway.merchantwebhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.merchantwebhook.entity.MerchantWebhookEntity;


public interface MerchantWebhookRepository extends JpaRepository<MerchantWebhookEntity, Long> {
}
