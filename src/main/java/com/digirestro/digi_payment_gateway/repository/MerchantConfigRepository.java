package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantConfigRepository extends JpaRepository<MerchantConfigEntity, Long> {

    Optional<MerchantConfigEntity> findByMerchant_Id(Long merchantId);
}
