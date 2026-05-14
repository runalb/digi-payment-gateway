package com.runalb.ondemand_service.business.repository;

import com.runalb.ondemand_service.business.entity.BusinessConfigEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessConfigRepository extends JpaRepository<BusinessConfigEntity, Long> {

    Optional<BusinessConfigEntity> findByBusinessId(Long businessId);

    Optional<BusinessConfigEntity> findByBusinessIdAndIsDeletedFalse(Long businessId);

    boolean existsByBusinessIdAndIsDeletedFalse(Long businessId);
}
