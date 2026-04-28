package com.runalb.ondemand_service.merchant.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.merchant.entity.MerchantConfigEntity;

public interface MerchantConfigRepository extends JpaRepository<MerchantConfigEntity, Long> {

    Optional<MerchantConfigEntity> findByMerchant_Id(Long merchantId);
}
