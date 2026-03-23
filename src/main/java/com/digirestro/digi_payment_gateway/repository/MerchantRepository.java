package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.MerchantEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
    Optional<MerchantEntity> findByApiKey(String apiKey);
}
