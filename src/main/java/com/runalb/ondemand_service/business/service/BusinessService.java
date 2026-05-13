package com.runalb.ondemand_service.business.service;

import com.runalb.ondemand_service.business.dto.BusinessConfigCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessConfigResponse;
import com.runalb.ondemand_service.business.dto.BusinessConfigUpdateRequest;
import com.runalb.ondemand_service.business.dto.BusinessCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessPaymentChannelConfigCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessPaymentChannelConfigResponse;
import com.runalb.ondemand_service.business.dto.BusinessPaymentChannelConfigUpdateRequest;
import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.dto.BusinessUpdateRequest;
import com.runalb.ondemand_service.business.entity.BusinessConfigEntity;
import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.business.entity.BusinessPaymentChannelConfigEntity;
import com.runalb.ondemand_service.business.repository.BusinessConfigRepository;
import com.runalb.ondemand_service.business.repository.BusinessPaymentChannelConfigRepository;
import com.runalb.ondemand_service.business.repository.BusinessRepository;
// import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
// import com.runalb.ondemand_service.payment.service.PaymentChannelService;
import com.runalb.ondemand_service.user.service.UserService;
import com.runalb.ondemand_service.util.InputSanitizer;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessConfigRepository businessConfigRepository;
    // private final PaymentChannelService paymentChannelService;
    private final BusinessPaymentChannelConfigRepository businessPaymentChannelConfigRepository;
    private final UserService userService;

    public BusinessService(
            BusinessRepository businessRepository,
            BusinessConfigRepository businessConfigRepository,
            // PaymentChannelService paymentChannelService,
            BusinessPaymentChannelConfigRepository businessPaymentChannelConfigRepository,
            UserService userService) {
        this.businessRepository = businessRepository;
        this.businessConfigRepository = businessConfigRepository;
        // this.paymentChannelService = paymentChannelService;
        this.businessPaymentChannelConfigRepository = businessPaymentChannelConfigRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public BusinessConfigEntity findBusinessConfigByBusinessId(Long businessId) {
        return businessConfigRepository
                .findByBusiness_IdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new EntityNotFoundException("Business configuration not found"));
    }

    @Transactional(readOnly = true)
    public BusinessPaymentChannelConfigEntity findActivePaymentChannelConfigByBusinessId(Long businessId) {
        return businessPaymentChannelConfigRepository
                .findFirstByBusiness_IdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Active business payment channel configuration not found"));
    }

    @Transactional
    public BusinessResponse createBusiness(BusinessCreateRequest request, Long ownerUserId) {
        BusinessEntity business = new BusinessEntity();
        business.setName(InputSanitizer.normalizeName(request.name()));
        business.setApiKey(UUID.randomUUID().toString());
        business.setEmail(InputSanitizer.normalizeEmail(request.email()));
        business = businessRepository.save(business);

        userService.linkUserToBusiness(ownerUserId, business);

        return new BusinessResponse(
                business.getId(), business.getName(), business.getEmail(), business.getApiKey(), business.getIsDeleted());
    }

    @Transactional(readOnly = true)
    public List<BusinessResponse> listBusinessesForUser(Long userId) {
        return businessRepository.findByUsers_IdOrderByIdAsc(userId).stream()
                .map(BusinessService::toBusinessResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessResponse getBusiness(Long businessId) {
        BusinessEntity business = businessRepository
                .findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
        return toBusinessResponse(business);
    }

    @Transactional
    public BusinessResponse updateBusiness(Long businessId, BusinessUpdateRequest request) {
        if (request == null
                || (request.name() == null && request.email() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        BusinessEntity business = businessRepository
                .findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        if (request.name() != null) {
            business.setName(InputSanitizer.normalizeName(request.name()));
        }
        if (request.email() != null) {
            String email = InputSanitizer.normalizeEmail(request.email());
            businessRepository
                    .findByEmail(email)
                    .filter(other -> !other.getId().equals(businessId))
                    .ifPresent(other -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
                    });
            business.setEmail(email);
        }

        business = businessRepository.save(business);
        return toBusinessResponse(business);
    }

    private static BusinessResponse toBusinessResponse(BusinessEntity business) {
        return new BusinessResponse(
                business.getId(), business.getName(), business.getEmail(), business.getApiKey(), business.getIsDeleted());
    }

    @Transactional(readOnly = true)
    public BusinessConfigResponse getBusinessConfig(Long businessId) {
        BusinessConfigEntity config = businessConfigRepository
                .findByBusiness_IdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business configuration not found"));
        return new BusinessConfigResponse(businessId, config.getCurrency(), config.getWebhookUrl());
    }

    @Transactional
    public BusinessConfigResponse createBusinessConfig(Long businessId, BusinessConfigCreateRequest request) {
        BusinessEntity business = businessRepository
                .findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        if (businessConfigRepository.findByBusiness_IdAndIsDeletedFalse(businessId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Business configuration already exists");
        }

        String currency = InputSanitizer.normalizeISO4217Currency(request.currency());
        String webhookUrl = StringUtils.hasText(request.webhookUrl()) ? request.webhookUrl().trim() : null;

        BusinessConfigEntity entity = businessConfigRepository
                .findByBusiness_Id(businessId)
                .orElseGet(BusinessConfigEntity::new);
        entity.setBusiness(business);
        entity.setCurrency(currency);
        entity.setWebhookUrl(webhookUrl);
        entity.setIsDeleted(Boolean.FALSE);
        businessConfigRepository.save(entity);

        return new BusinessConfigResponse(businessId, currency, webhookUrl);
    }

    @Transactional
    public BusinessConfigResponse updateBusinessConfig(Long businessId, BusinessConfigUpdateRequest request) {
        BusinessConfigEntity entity = businessConfigRepository
                .findByBusiness_IdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business configuration not found"));

        if (StringUtils.hasText(request.currency())) {
            entity.setCurrency(InputSanitizer.normalizeISO4217Currency(request.currency()));
        }
        if (request.webhookUrl() != null) {
            entity.setWebhookUrl(StringUtils.hasText(request.webhookUrl()) ? request.webhookUrl().trim() : null);
        }
        businessConfigRepository.save(entity);

        return new BusinessConfigResponse(businessId, entity.getCurrency(), entity.getWebhookUrl());
    }

    @Transactional
    public BusinessPaymentChannelConfigResponse createBusinessPaymentChannelConfig(Long businessId, BusinessPaymentChannelConfigCreateRequest request) {
        BusinessEntity business = businessRepository
                .findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        // PaymentChannelEntity paymentChannel = paymentChannelService.findById(request.paymentChannelId());

        // if (businessPaymentChannelConfigRepository.existsByBusiness_Id(businessId)) {
        //     // (businessPaymentChannelConfigRepository.existsByBusiness_IdAndPaymentChannel_Id(businessId, paymentChannel.getId())) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "Business already has configuration for this payment channel");
        // }

        String configJson = StringUtils.hasText(request.configJson()) ? request.configJson() : null;

        BusinessPaymentChannelConfigEntity entity = new BusinessPaymentChannelConfigEntity();
        entity.setBusiness(business);
        // entity.setPaymentChannel(paymentChannel);
        entity.setConfigJson(configJson);
        entity = businessPaymentChannelConfigRepository.save(entity);

        return toBusinessPaymentChannelConfigResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<BusinessPaymentChannelConfigResponse> listBusinessPaymentChannelConfigs(Long businessId) {
        if (!businessRepository.existsById(businessId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found");
        }
        return businessPaymentChannelConfigRepository.findByBusiness_IdOrderByIdAsc(businessId).stream()
                .map(BusinessService::toBusinessPaymentChannelConfigResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessPaymentChannelConfigResponse getBusinessPaymentChannelConfig(Long businessId, Long configId) {
        BusinessPaymentChannelConfigEntity entity = businessPaymentChannelConfigRepository
                .findByIdAndBusiness_Id(configId, businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business payment channel config not found"));
        return toBusinessPaymentChannelConfigResponse(entity);
    }

    @Transactional
    public BusinessPaymentChannelConfigResponse updateBusinessPaymentChannelConfig(
            Long businessId, Long configId, BusinessPaymentChannelConfigUpdateRequest request) {
        if (request == null || (request.isDeleted() == null && request.configJson() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        BusinessPaymentChannelConfigEntity entity = businessPaymentChannelConfigRepository
                .findByIdAndBusiness_Id(configId, businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business payment channel config not found"));

        if (request.isDeleted() != null) {
            entity.setIsDeleted(request.isDeleted());
        }
        if (request.configJson() != null) {
            entity.setConfigJson(StringUtils.hasText(request.configJson()) ? request.configJson() : null);
        }
        entity = businessPaymentChannelConfigRepository.save(entity);
        return toBusinessPaymentChannelConfigResponse(entity);
    }

    private static BusinessPaymentChannelConfigResponse toBusinessPaymentChannelConfigResponse(
            BusinessPaymentChannelConfigEntity entity) {
        // PaymentChannelEntity paymentChannel = entity.getPaymentChannel();
        return new BusinessPaymentChannelConfigResponse(
                entity.getId(),
                entity.getBusiness().getId(),
                // paymentChannel.getId(),
                // paymentChannel.getName(),
                entity.getConfigJson());
    }

    @Transactional
    public void deactivateBusinessConfig(Long businessId) {
        BusinessConfigEntity config = businessConfigRepository
                .findByBusiness_IdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business configuration not found"));
        config.setIsDeleted(true);
        businessConfigRepository.save(config);
    }

    @Transactional
    public void deactivateBusiness(Long businessId) {
        BusinessEntity business = businessRepository
                .findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
        business.setIsDeleted(true);
        businessRepository.save(business);
    }

    @Transactional
    public void deactivateBusinessPaymentChannelConfig(Long businessId, Long configId) {
        BusinessPaymentChannelConfigEntity config = businessPaymentChannelConfigRepository
                .findByIdAndBusiness_Id(configId, businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business payment channel config not found"));
        config.setIsDeleted(true);
        businessPaymentChannelConfigRepository.save(config);
    }
}
