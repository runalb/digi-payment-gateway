package com.runalb.ondemand_service.merchant.service;

import com.runalb.ondemand_service.merchant.dto.MerchantConfigCreateRequest;
import com.runalb.ondemand_service.merchant.dto.MerchantConfigResponse;
import com.runalb.ondemand_service.merchant.dto.MerchantConfigUpdateRequest;
import com.runalb.ondemand_service.merchant.dto.MerchantCreateRequest;
import com.runalb.ondemand_service.merchant.dto.MerchantPaymentChannelConfigCreateRequest;
import com.runalb.ondemand_service.merchant.dto.MerchantPaymentChannelConfigResponse;
import com.runalb.ondemand_service.merchant.dto.MerchantPaymentChannelConfigUpdateRequest;
import com.runalb.ondemand_service.merchant.dto.MerchantResponse;
import com.runalb.ondemand_service.merchant.dto.MerchantUpdateRequest;
import com.runalb.ondemand_service.merchant.entity.MerchantConfigEntity;
import com.runalb.ondemand_service.merchant.entity.MerchantEntity;
import com.runalb.ondemand_service.merchant.entity.MerchantPaymentChannelConfigEntity;
import com.runalb.ondemand_service.merchant.repository.MerchantConfigRepository;
import com.runalb.ondemand_service.merchant.repository.MerchantPaymentChannelConfigRepository;
import com.runalb.ondemand_service.merchant.repository.MerchantRepository;
import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
import com.runalb.ondemand_service.payment.service.PaymentChannelService;
import com.runalb.ondemand_service.user.service.UserService;
import com.runalb.ondemand_service.util.StringNormalizer;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MerchantService {

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
    public MerchantResponse createMerchant(MerchantCreateRequest request, Long ownerUserId) {
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName(StringNormalizer.normalizeName(request.name()));
        merchant.setApiKey(UUID.randomUUID().toString());
        merchant.setIsActive(true);
        merchant.setEmail(StringNormalizer.normalizeEmail(request.email()));
        merchant = merchantRepository.save(merchant);

        userService.linkMerchantToUser(ownerUserId, merchant);

        return new MerchantResponse(
                merchant.getId(), merchant.getName(), merchant.getEmail(), merchant.getApiKey(), merchant.getIsActive());
    }

    @Transactional(readOnly = true)
    public List<MerchantResponse> listMerchantsForUser(Long userId) {
        return merchantRepository.findByUsers_IdOrderByIdAsc(userId).stream()
                .map(MerchantService::toMerchantResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MerchantResponse getMerchant(Long merchantId) {
        MerchantEntity merchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));
        return toMerchantResponse(merchant);
    }

    @Transactional
    public MerchantResponse updateMerchant(Long merchantId, MerchantUpdateRequest request) {
        if (request == null
                || (request.name() == null && request.email() == null && request.isActive() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        MerchantEntity merchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));

        if (request.name() != null) {
            merchant.setName(StringNormalizer.normalizeName(request.name()));
        }
        if (request.email() != null) {
            String email = StringNormalizer.normalizeEmail(request.email());
            merchantRepository
                    .findByEmail(email)
                    .filter(other -> !other.getId().equals(merchantId))
                    .ifPresent(other -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
                    });
            merchant.setEmail(email);
        }
        if (request.isActive() != null) {
            merchant.setIsActive(request.isActive());
        }

        merchant = merchantRepository.save(merchant);
        return toMerchantResponse(merchant);
    }

    private static MerchantResponse toMerchantResponse(MerchantEntity merchant) {
        return new MerchantResponse(
                merchant.getId(), merchant.getName(), merchant.getEmail(), merchant.getApiKey(), merchant.getIsActive());
    }

    @Transactional(readOnly = true)
    public MerchantConfigResponse getMerchantConfig(Long merchantId) {
        MerchantConfigEntity config = merchantConfigRepository
                .findByMerchant_Id(merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Merchant configuration not found"));
        return new MerchantConfigResponse(merchantId, config.getCurrency(), config.getWebhookUrl());
    }

    @Transactional
    public MerchantConfigResponse createMerchantConfig(Long merchantId, MerchantConfigCreateRequest request) {
        MerchantEntity merchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));

        if (merchantConfigRepository.findByMerchant_Id(merchantId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Merchant configuration already exists");
        }

        String currency = StringNormalizer.normalizeISO4217Currency(request.currency());
        String webhookUrl = StringUtils.hasText(request.webhookUrl()) ? request.webhookUrl().trim() : null;

        MerchantConfigEntity entity = new MerchantConfigEntity();
        entity.setMerchant(merchant);
        entity.setCurrency(currency);
        entity.setWebhookUrl(webhookUrl);
        merchantConfigRepository.save(entity);

        return new MerchantConfigResponse(merchantId, currency, webhookUrl);
    }

    @Transactional
    public MerchantConfigResponse updateMerchantConfig(Long merchantId, MerchantConfigUpdateRequest request) {
        MerchantConfigEntity entity = merchantConfigRepository
                .findByMerchant_Id(merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Merchant configuration not found"));

        if (StringUtils.hasText(request.currency())) {
            entity.setCurrency(StringNormalizer.normalizeISO4217Currency(request.currency()));
        }
        if (request.webhookUrl() != null) {
            entity.setWebhookUrl(StringUtils.hasText(request.webhookUrl()) ? request.webhookUrl().trim() : null);
        }
        merchantConfigRepository.save(entity);

        return new MerchantConfigResponse(merchantId, entity.getCurrency(), entity.getWebhookUrl());
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

        return toMerchantPaymentChannelConfigResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<MerchantPaymentChannelConfigResponse> listMerchantPaymentChannelConfigs(Long merchantId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found");
        }
        return merchantPaymentChannelConfigRepository.findByMerchant_IdOrderByIdAsc(merchantId).stream()
                .map(MerchantService::toMerchantPaymentChannelConfigResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MerchantPaymentChannelConfigResponse getMerchantPaymentChannelConfig(Long merchantId, Long configId) {
        MerchantPaymentChannelConfigEntity entity = merchantPaymentChannelConfigRepository
                .findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Merchant payment channel config not found"));
        return toMerchantPaymentChannelConfigResponse(entity);
    }

    @Transactional
    public MerchantPaymentChannelConfigResponse updateMerchantPaymentChannelConfig(
            Long merchantId, Long configId, MerchantPaymentChannelConfigUpdateRequest request) {
        if (request == null || (request.isActive() == null && request.configJson() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        MerchantPaymentChannelConfigEntity entity = merchantPaymentChannelConfigRepository
                .findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Merchant payment channel config not found"));

        if (request.isActive() != null) {
            entity.setIsActive(request.isActive());
        }
        if (request.configJson() != null) {
            entity.setConfigJson(StringUtils.hasText(request.configJson()) ? request.configJson() : null);
        }
        entity = merchantPaymentChannelConfigRepository.save(entity);
        return toMerchantPaymentChannelConfigResponse(entity);
    }

    private static MerchantPaymentChannelConfigResponse toMerchantPaymentChannelConfigResponse(
            MerchantPaymentChannelConfigEntity entity) {
        PaymentChannelEntity paymentChannel = entity.getPaymentChannel();
        return new MerchantPaymentChannelConfigResponse(
                entity.getId(),
                entity.getMerchant().getId(),
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
