package com.digirestro.digi_payment_gateway.payment.repository;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentChannelNameEnum;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentChannelRepository extends JpaRepository<PaymentChannelEntity, Long> {
    Optional<PaymentChannelEntity> findByName(PaymentChannelNameEnum name);
}
