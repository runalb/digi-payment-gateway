package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.MerchantPaymentChannelConfigEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantPaymentChannelConfigRepository extends JpaRepository<MerchantPaymentChannelConfigEntity, Long> {
    Optional<MerchantPaymentChannelConfigEntity> findFirstByMerchant_IdAndIsActiveTrue(Long merchantId);

    boolean existsByMerchant_IdAndPaymentChannel_Id(Long merchantId, Long paymentChannelId);

    Optional<MerchantPaymentChannelConfigEntity> findByIdAndMerchant_Id(Long id, Long merchantId);

    List<MerchantPaymentChannelConfigEntity> findByMerchant_IdOrderByIdAsc(Long merchantId);
}
