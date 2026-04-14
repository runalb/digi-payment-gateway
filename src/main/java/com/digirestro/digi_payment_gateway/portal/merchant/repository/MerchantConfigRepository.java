package com.digirestro.digi_payment_gateway.portal.merchant.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.portal.merchant.entity.MerchantConfigEntity;

public interface MerchantConfigRepository extends JpaRepository<MerchantConfigEntity, Long> {

    Optional<MerchantConfigEntity> findByMerchant_Id(Long merchantId);
}
