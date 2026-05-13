package com.runalb.ondemand_service.business.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.business.entity.BusinessConfigEntity;

public interface BusinessConfigRepository extends JpaRepository<BusinessConfigEntity, Long> {

    Optional<BusinessConfigEntity> findByBusiness_Id(Long businessId);

    Optional<BusinessConfigEntity> findByBusiness_IdAndIsDeletedFalse(Long businessId);
}
