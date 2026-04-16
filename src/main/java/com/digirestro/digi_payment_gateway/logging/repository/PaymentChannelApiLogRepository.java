package com.digirestro.digi_payment_gateway.logging.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.logging.entity.PaymentChannelApiLogEntity;

public interface PaymentChannelApiLogRepository extends JpaRepository<PaymentChannelApiLogEntity, Long> {
}
