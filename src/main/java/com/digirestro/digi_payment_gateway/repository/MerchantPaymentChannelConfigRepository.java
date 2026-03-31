package com.digirestro.digi_payment_gateway.repository;

import com.digirestro.digi_payment_gateway.entity.MerchantPaymentChannelConfigEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantPaymentChannelConfigRepository extends JpaRepository<MerchantPaymentChannelConfigEntity, Long> {
    Optional<MerchantPaymentChannelConfigEntity> findFirstByMerchant_IdAndIsActiveTrue(Long merchantId);

    boolean existsByMerchant_IdAndPaymentChannel_Id(Long merchantId, Long paymentChannelId);
}
