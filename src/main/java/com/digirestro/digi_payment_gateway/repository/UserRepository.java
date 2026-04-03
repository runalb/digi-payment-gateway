package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByMobileNumber(String mobileNumber);
    boolean existsByMobileNumber(String mobileNumber);
}
