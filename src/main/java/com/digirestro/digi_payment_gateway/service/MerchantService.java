package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationResponse;
import com.digirestro.digi_payment_gateway.dto.MerchantPaymentChannelConfigCreateRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantPaymentChannelConfigResponse;
import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.repository.MerchantConfigRepository;
import com.digirestro.digi_payment_gateway.repository.MerchantPaymentChannelConfigRepository;
import com.digirestro.digi_payment_gateway.repository.MerchantRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MerchantService {
    private static final String DEFAULT_CURRENCY = "USD";

    private final MerchantRepository merchantRepository;
    private final MerchantConfigRepository merchantConfigRepository;
    private final PaymentChannelService paymentChannelService;
    private final MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository;
    private final UserService userService;

    public MerchantService(
            MerchantRepository merchantRepository,
            MerchantConfigRepository merchantConfigRepository,
            PaymentChannelService paymentChannelService,
            MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository,
            UserService userService) {
        this.merchantRepository = merchantRepository;
        this.merchantConfigRepository = merchantConfigRepository;
        this.paymentChannelService = paymentChannelService;
        this.merchantPaymentChannelConfigRepository = merchantPaymentChannelConfigRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public MerchantConfigEntity findMerchantConfigByMerchantId(Long merchantId) {
        return merchantConfigRepository
                .findByMerchant_Id(merchantId)
                .orElseThrow(() -> new EntityNotFoundException("Merchant configuration not found"));
    }

    @Transactional(readOnly = true)
    public MerchantPaymentChannelConfigEntity findActivePaymentChannelConfigByMerchantId(Long merchantId) {
        return merchantPaymentChannelConfigRepository
                .findFirstByMerchant_IdAndIsActiveTrue(merchantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Active merchant payment channel configuration not found"));
    }

    @Transactional
    public MerchantRegistrationResponse createMerchant(MerchantRegistrationRequest request, Long ownerUserId) {
        String currency = StringUtils.hasText(request.currency())
                ? request.currency().trim().toUpperCase()
                : DEFAULT_CURRENCY;
        String webhookUrl = StringUtils.hasText(request.webhookUrl()) ? request.webhookUrl().trim() : null;

        MerchantEntity merchant = new MerchantEntity();
        merchant.setName(request.name().trim());
        merchant.setApiKey(UUID.randomUUID().toString());
        merchant.setIsActive(true);
        merchant = merchantRepository.save(merchant);

        userService.linkMerchantToUser(ownerUserId, merchant);

        MerchantConfigEntity config = new MerchantConfigEntity();
        config.setMerchant(merchant);
        config.setCurrency(currency);
        config.setWebhookUrl(webhookUrl);
        merchantConfigRepository.save(config);

        return new MerchantRegistrationResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getApiKey(),
                merchant.getIsActive(),
                currency,
                webhookUrl);
    }

    @Transactional
    public MerchantPaymentChannelConfigResponse createMerchantPaymentChannelConfig(Long merchantId, MerchantPaymentChannelConfigCreateRequest request) {
        MerchantEntity merchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));

        PaymentChannelEntity paymentChannel = paymentChannelService.findById(request.paymentChannelId());

        if (merchantPaymentChannelConfigRepository.existsByMerchant_IdAndPaymentChannel_Id(merchantId, paymentChannel.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Merchant already has configuration for this payment channel");
        }

        String configJson = StringUtils.hasText(request.configJson()) ? request.configJson() : null;

        MerchantPaymentChannelConfigEntity entity = new MerchantPaymentChannelConfigEntity();
        entity.setMerchant(merchant);
        entity.setPaymentChannel(paymentChannel);
        entity.setIsActive(Boolean.TRUE);
        entity.setConfigJson(configJson);
        entity = merchantPaymentChannelConfigRepository.save(entity);

        return new MerchantPaymentChannelConfigResponse(
                entity.getId(),
                merchant.getId(),
                paymentChannel.getId(),
                paymentChannel.getName(),
                entity.getIsActive(),
                entity.getConfigJson());
    }

    @Transactional
    public void deactivateMerchant(Long merchantId) {
        MerchantEntity merchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));
        merchant.setIsActive(false);
        merchantRepository.save(merchant);
    }

    @Transactional
    public void deactivateMerchantPaymentChannelConfig(Long merchantId, Long configId) {
        MerchantPaymentChannelConfigEntity config = merchantPaymentChannelConfigRepository
                .findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Merchant payment channel config not found"));
        config.setIsActive(false);
        merchantPaymentChannelConfigRepository.save(config);
    }
}
