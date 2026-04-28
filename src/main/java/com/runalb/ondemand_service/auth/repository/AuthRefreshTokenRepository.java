package com.runalb.ondemand_service.auth.repository;

import com.runalb.ondemand_service.auth.entity.AuthRefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshTokenEntity, Long> {
    Optional<AuthRefreshTokenEntity> findByTokenHashAndRevokedAtIsNull(String tokenHash);
}
