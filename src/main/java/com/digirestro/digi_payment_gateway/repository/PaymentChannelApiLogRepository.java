package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.PaymentChannelApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentChannelApiLogRepository extends JpaRepository<PaymentChannelApiLogEntity, Long> {
}
