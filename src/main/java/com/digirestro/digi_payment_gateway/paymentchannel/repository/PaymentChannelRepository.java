package com.digirestro.digi_payment_gateway.paymentchannel.repository;

import com.digirestro.digi_payment_gateway.paymentchannel.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.paymentchannel.enums.PaymentChannelNameEnum;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentChannelRepository extends JpaRepository<PaymentChannelEntity, Long> {
    Optional<PaymentChannelEntity> findByName(PaymentChannelNameEnum name);
}
