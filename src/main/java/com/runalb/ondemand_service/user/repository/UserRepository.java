package com.runalb.ondemand_service.user.repository;

import com.runalb.ondemand_service.user.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);

    Optional<UserEntity> findByMobileNumber(String mobileNumber);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.mobileNumber = :mobileNumber")
    Optional<UserEntity> findByMobileNumberWithRoles(@Param("mobileNumber") String mobileNumber);

    @Query("SELECT u FROM UserEntity u WHERE SIZE(u.roles) = 0")
    List<UserEntity> findAllWithoutRoles();

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByIdAndMerchants_Id(Long id, Long merchantsId);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<UserEntity> findByIdWithRoles(@Param("id") Long id);

    boolean existsByIdAndBusinesses_Id(Long id, Long businessesId);
}
