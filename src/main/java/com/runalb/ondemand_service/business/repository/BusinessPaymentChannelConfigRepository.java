package com.runalb.ondemand_service.business.repository;

import com.runalb.ondemand_service.business.entity.BusinessPaymentChannelConfigEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessPaymentChannelConfigRepository
        extends JpaRepository<BusinessPaymentChannelConfigEntity, Long> {

    Optional<BusinessPaymentChannelConfigEntity> findFirstByBusinessIdAndIsDeletedFalseOrderByIdAsc(Long businessId);

    boolean existsByBusinessIdAndIsDeletedFalse(Long businessId);

    Optional<BusinessPaymentChannelConfigEntity> findByIdAndBusinessId(Long id, Long businessId);

    Optional<BusinessPaymentChannelConfigEntity> findByIdAndBusinessIdAndIsDeletedFalse(Long id, Long businessId);

    List<BusinessPaymentChannelConfigEntity> findByBusinessIdAndIsDeletedFalseOrderByIdAsc(Long businessId);
}
