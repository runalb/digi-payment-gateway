package com.digirestro.digi_payment_gateway.merchant.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.merchant.entity.MerchantPaymentChannelConfigEntity;

public interface MerchantPaymentChannelConfigRepository extends JpaRepository<MerchantPaymentChannelConfigEntity, Long> {

    Optional<MerchantPaymentChannelConfigEntity> findByMerchant_IdAndIsActiveTrue(Long merchantId);
}
