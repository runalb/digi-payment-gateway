package com.runalb.ondemand_service.merchant.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.merchant.entity.MerchantEntity;

public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
    Optional<MerchantEntity> findByApiKey(String apiKey);

    Optional<MerchantEntity> findByEmail(String email);

    List<MerchantEntity> findByUsers_IdOrderByIdAsc(Long userId);
}
