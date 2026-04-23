package com.digirestro.digi_payment_gateway.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByMobileNumber(String mobileNumber);
    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByIdAndMerchants_Id(Long id, Long merchantsId);
}
