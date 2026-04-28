package com.runalb.ondemand_service.logging.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.logging.entity.PaymentChannelApiLogEntity;

public interface PaymentChannelApiLogRepository extends JpaRepository<PaymentChannelApiLogEntity, Long> {
}
