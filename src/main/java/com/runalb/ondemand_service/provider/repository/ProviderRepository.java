package com.runalb.ondemand_service.provider.repository;

import com.runalb.ondemand_service.provider.entity.ProviderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    boolean existsByUser_Id(Long userId);

    @Query("SELECT DISTINCT p FROM ProviderEntity p JOIN FETCH p.user u LEFT JOIN FETCH u.roles WHERE p.id = :id")
    Optional<ProviderEntity> findByIdWithUserAndRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM ProviderEntity p JOIN FETCH p.user u LEFT JOIN FETCH u.roles WHERE p.user.id = :userId")
    Optional<ProviderEntity> findByUserIdWithUserAndRoles(@Param("userId") Long userId);
}
