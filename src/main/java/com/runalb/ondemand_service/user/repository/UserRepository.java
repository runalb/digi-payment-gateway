package com.runalb.ondemand_service.user.repository;

import com.runalb.ondemand_service.user.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByIdAndIsDeletedFalse(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findWithRolesByIdAndIsDeletedFalse(Long id);

    Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findWithRolesByEmailAndIsDeletedFalse(String email);

    Optional<UserEntity> findByMobileNumberAndIsDeletedFalse(String mobileNumber);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findWithRolesByMobileNumberAndIsDeletedFalse(String mobileNumber);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByIdAndBusinesses_Id(Long id, Long businessId);
}
