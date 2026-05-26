package com.digirestro.digi_payment_gateway.payment_channel.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.payment_channel.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;

public interface PaymentChannelRepository extends JpaRepository<PaymentChannelEntity, Long> {
    Optional<PaymentChannelEntity> findByName(PaymentChannelNameEnum name);
}
