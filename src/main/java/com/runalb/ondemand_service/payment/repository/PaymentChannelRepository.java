package com.runalb.ondemand_service.payment.repository;

import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentChannelRepository extends JpaRepository<PaymentChannelEntity, Long> {
    Optional<PaymentChannelEntity> findByName(PaymentChannelNameEnum name);
}
