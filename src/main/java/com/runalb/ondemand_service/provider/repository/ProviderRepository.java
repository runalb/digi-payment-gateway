package com.runalb.ondemand_service.provider.repository;

import com.runalb.ondemand_service.provider.entity.ProviderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    boolean existsByUserId(Long userId);

    boolean existsByIdAndUserId(Long providerId, Long userId);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<ProviderEntity> findWithUserAndRolesById(Long id);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<ProviderEntity> findWithUserAndRolesByIdAndIsDeletedFalse(Long id);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<ProviderEntity> findWithUserAndRolesByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    List<ProviderEntity> findWithUserAndRolesByIsDeletedFalseOrderByIdAsc();
}
