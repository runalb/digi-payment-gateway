package com.digirestro.digi_payment_gateway.paymentchannelwebhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.paymentchannelwebhook.entity.WebhookIncomingPaymentChannelLogEntity;

public interface WebhookIncomingPaymentChannelLogRepository extends JpaRepository<WebhookIncomingPaymentChannelLogEntity, Long> {
}
