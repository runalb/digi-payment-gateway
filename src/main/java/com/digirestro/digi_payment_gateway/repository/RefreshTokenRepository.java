package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenHashAndRevokedAtIsNull(String tokenHash);
}
