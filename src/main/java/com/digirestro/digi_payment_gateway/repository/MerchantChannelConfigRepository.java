package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.MerchantChannelConfigEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantChannelConfigRepository extends JpaRepository<MerchantChannelConfigEntity, Long> {
    Optional<MerchantChannelConfigEntity> findFirstByMerchant_IdAndIsActiveTrue(Long merchantId);
}
