package com.digirestro.digi_payment_gateway.integration.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.integration.channel.entity.PaymentChannelApiLogEntity;

public interface PaymentChannelApiLogRepository extends JpaRepository<PaymentChannelApiLogEntity, Long> {
}
