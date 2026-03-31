package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.WebhookIncomingPaymentChannelLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookIncomingPaymentChannelLogRepository extends JpaRepository<WebhookIncomingPaymentChannelLogEntity, Long> {
}
