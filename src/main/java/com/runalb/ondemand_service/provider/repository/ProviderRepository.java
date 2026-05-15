package com.runalb.ondemand_service.provider.repository;

import com.runalb.ondemand_service.provider.entity.ProviderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    boolean existsByUser_Id(Long userId);

    boolean existsByIdAndUser_Id(Long providerId, Long userId);

    boolean existsByIdAndUser_IdAndIsDeletedFalse(Long id, Long userId);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<ProviderEntity> findWithUserAndRolesById(Long id);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<ProviderEntity> findWithUserAndRolesByIdAndIsDeletedFalse(Long id);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<ProviderEntity> findWithUserAndRolesByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    List<ProviderEntity> findWithUserAndRolesByIsDeletedFalseOrderByIdAsc();
}
