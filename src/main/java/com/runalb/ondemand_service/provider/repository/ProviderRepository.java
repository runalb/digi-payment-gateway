package com.runalb.ondemand_service.provider.repository;

import com.runalb.ondemand_service.provider.entity.ProviderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    boolean existsByUser_Id(Long userId);

    boolean existsByIdAndUser_Id(Long providerId, Long userId);

    @Query("SELECT DISTINCT p FROM ProviderEntity p JOIN FETCH p.user u LEFT JOIN FETCH u.roles WHERE p.id = :id")
    Optional<ProviderEntity> findByIdWithUserAndRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM ProviderEntity p JOIN FETCH p.user u LEFT JOIN FETCH u.roles WHERE p.user.id = :userId")
    Optional<ProviderEntity> findByUserIdWithUserAndRoles(@Param("userId") Long userId);

    @Query(
            "SELECT DISTINCT p FROM ProviderEntity p JOIN FETCH p.user u LEFT JOIN FETCH u.roles WHERE p.isActive = true ORDER BY p.id")
    List<ProviderEntity> findAllActiveWithUserAndRoles();
}
