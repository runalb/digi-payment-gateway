package com.runalb.ondemand_service.business.repository;

import com.runalb.ondemand_service.business.entity.BusinessEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<BusinessEntity, Long> {

    Optional<BusinessEntity> findByIdAndIsDeletedFalse(Long id);

    Optional<BusinessEntity> findByApiKeyAndIsDeletedFalse(String apiKey);

    Optional<BusinessEntity> findByEmail(String email);

    List<BusinessEntity> findByUsers_IdAndIsDeletedFalseOrderByIdAsc(Long userId);

    boolean existsByIdAndUsers_IdAndIsDeletedFalse(Long id, Long userId);
}
