package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    java.util.Optional<PaymentEntity> findByIdAndMerchant_Id(Long id, Long merchantId);

    java.util.List<PaymentEntity> findAllByMerchant_IdOrderByCreatedDateTimeDesc(Long merchantId);
}
