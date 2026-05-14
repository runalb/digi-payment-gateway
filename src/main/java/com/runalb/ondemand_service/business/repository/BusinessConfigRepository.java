package com.runalb.ondemand_service.business.repository;

import com.runalb.ondemand_service.business.entity.BusinessConfigEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessConfigRepository extends JpaRepository<BusinessConfigEntity, Long> {

    Optional<BusinessConfigEntity> findByBusiness_Id(Long businessId);

    Optional<BusinessConfigEntity> findByBusiness_IdAndIsDeletedFalse(Long businessId);

    boolean existsByBusiness_IdAndIsDeletedFalse(Long businessId);
}
