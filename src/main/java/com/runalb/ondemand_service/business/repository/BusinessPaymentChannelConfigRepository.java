package com.runalb.ondemand_service.business.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.runalb.ondemand_service.business.entity.BusinessPaymentChannelConfigEntity;

public interface BusinessPaymentChannelConfigRepository extends JpaRepository<BusinessPaymentChannelConfigEntity, Long> {
    Optional<BusinessPaymentChannelConfigEntity> findFirstByBusiness_IdAndIsDeletedFalse(Long businessId);

    // boolean existsByBusiness_IdAndPaymentChannel_Id(Long businessId, Long paymentChannelId);

    Optional<BusinessPaymentChannelConfigEntity> findByIdAndBusiness_Id(Long id, Long businessId);

    List<BusinessPaymentChannelConfigEntity> findByBusiness_IdOrderByIdAsc(Long businessId);
}
