package com.digirestro.digi_payment_gateway.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    java.util.Optional<PaymentEntity> findByIdAndMerchantId(Long id, Long merchantId);

    java.util.List<PaymentEntity> findAllByMerchantIdOrderByCreatedDateTimeDesc(Long merchantId);
}
