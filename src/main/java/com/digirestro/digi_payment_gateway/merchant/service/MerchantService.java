package com.digirestro.digi_payment_gateway.merchant.service;

import com.digirestro.digi_payment_gateway.merchant.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.repository.MerchantConfigRepository;
import com.digirestro.digi_payment_gateway.merchant.repository.MerchantPaymentChannelConfigRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantService {

    private final MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository;
    private final MerchantConfigRepository merchantConfigRepository;

    public MerchantService(
            MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository,
            MerchantConfigRepository merchantConfigRepository) {
        this.merchantPaymentChannelConfigRepository = merchantPaymentChannelConfigRepository;
        this.merchantConfigRepository = merchantConfigRepository;
    }

    @Transactional(readOnly = true)
    public MerchantPaymentChannelConfigEntity findPaymentChannelConfigByMerchantId(Long merchantId) {
        return merchantPaymentChannelConfigRepository.findByMerchant_IdAndIsActiveTrue(merchantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Active payment channel config not found for merchantId: " + merchantId));
    }

    @Transactional(readOnly = true)
    public MerchantConfigEntity findMerchantConfigByMerchantId(Long merchantId) {
        return merchantConfigRepository.findByMerchant_Id(merchantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Merchant config not found for merchantId: " + merchantId));
    }
}
