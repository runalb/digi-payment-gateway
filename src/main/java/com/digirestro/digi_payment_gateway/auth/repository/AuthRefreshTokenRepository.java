package com.digirestro.digi_payment_gateway.auth.repository;

import com.digirestro.digi_payment_gateway.auth.entity.AuthRefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshTokenEntity, Long> {
    Optional<AuthRefreshTokenEntity> findByTokenHashAndRevokedAtIsNull(String tokenHash);
}
