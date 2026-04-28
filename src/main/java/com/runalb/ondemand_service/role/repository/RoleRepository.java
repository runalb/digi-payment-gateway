package com.runalb.ondemand_service.role.repository;

import com.runalb.ondemand_service.role.entity.RoleEntity;
import com.runalb.ondemand_service.role.enums.RoleNameEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleName(RoleNameEnum roleName);
}
