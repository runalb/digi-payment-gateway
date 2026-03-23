package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentChannelRepository extends JpaRepository<PaymentChannelEntity, Long> {
    Optional<PaymentChannelEntity> findByName(PaymentChannelNameEnum name);
}
