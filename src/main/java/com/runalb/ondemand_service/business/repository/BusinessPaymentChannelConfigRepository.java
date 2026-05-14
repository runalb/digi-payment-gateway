package com.runalb.ondemand_service.business.repository;

import com.runalb.ondemand_service.business.entity.BusinessPaymentChannelConfigEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessPaymentChannelConfigRepository
        extends JpaRepository<BusinessPaymentChannelConfigEntity, Long> {

    Optional<BusinessPaymentChannelConfigEntity> findFirstByBusiness_IdAndIsDeletedFalseOrderByIdAsc(Long businessId);

    boolean existsByBusiness_IdAndIsDeletedFalse(Long businessId);

    Optional<BusinessPaymentChannelConfigEntity> findByIdAndBusiness_Id(Long id, Long businessId);

    Optional<BusinessPaymentChannelConfigEntity> findByIdAndBusiness_IdAndIsDeletedFalse(Long id, Long businessId);

    List<BusinessPaymentChannelConfigEntity> findByBusiness_IdAndIsDeletedFalseOrderByIdAsc(Long businessId);
}
