package com.runalb.ondemand_service.business.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.business.entity.BusinessEntity;

public interface BusinessRepository extends JpaRepository<BusinessEntity, Long> {

    Optional<BusinessEntity> findByEmail(String email);
}
