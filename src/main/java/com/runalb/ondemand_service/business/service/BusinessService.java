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
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.security.CurrentUserService;
// import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
// import com.runalb.ondemand_service.payment.service.PaymentChannelService;
import com.runalb.ondemand_service.relationship.service.EntityLinkService;
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
    private final EntityLinkService entityLinkService;
    private final CurrentUserService currentUserService;

    public BusinessService(
            BusinessRepository businessRepository,
            BusinessConfigRepository businessConfigRepository,
            // PaymentChannelService paymentChannelService,
            BusinessPaymentChannelConfigRepository businessPaymentChannelConfigRepository,
            EntityLinkService entityLinkService,
            CurrentUserService currentUserService) {
        this.businessRepository = businessRepository;
        this.businessConfigRepository = businessConfigRepository;
        // this.paymentChannelService = paymentChannelService;
        this.businessPaymentChannelConfigRepository = businessPaymentChannelConfigRepository;
        this.entityLinkService = entityLinkService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public BusinessConfigEntity findBusinessConfigByBusinessId(Long businessId) {
        return businessConfigRepository
                .findByBusiness_IdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new EntityNotFoundException("Business configuration not found"));
    }


    @Transactional(readOnly = true)
    public BusinessPaymentChannelConfigEntity findPaymentChannelConfigByBusinessId(Long businessId) {
        return businessPaymentChannelConfigRepository
                .findFirstByBusiness_IdAndIsDeletedFalseOrderByIdAsc(businessId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "business payment channel configuration not found"));
    }

    @Transactional
    public BusinessResponse createBusiness(BusinessCreateRequest request) {
        UserEntity user = currentUserService.resolveAuthenticatedUser();

        String email = InputSanitizer.normalizeEmail(request.email());
        businessRepository
                .findByEmail(email)
                .ifPresent(other -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
                });

        String mobileNumber = InputSanitizer.trimToNull(request.mobileNumber());
        if (mobileNumber != null) {
            mobileNumber = InputSanitizer.normalizeMobile(mobileNumber);
            businessRepository
                    .findByMobileNumber(mobileNumber)
                    .ifPresent(other -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already in use");
                    });
        }

        BusinessEntity business = new BusinessEntity();
        business.setName(InputSanitizer.normalizeName(request.name()));
        business.setApiKey(UUID.randomUUID().toString());
        business.setEmail(email);
        business.setBusinessType(InputSanitizer.normalizeName(request.businessType()));
        business.setDescription(InputSanitizer.trimToNull(request.description()));
        business.setAddress(InputSanitizer.trimToNull(request.address()));
        business.setMobileNumber(mobileNumber);
        business.setIsVerified(Boolean.FALSE);
        business.setAverageRating(0.0);
        business = businessRepository.save(business);

        entityLinkService.linkUserToBusiness(user, business);

        return toBusinessResponse(business);
    }

    @Transactional(readOnly = true)
    public List<BusinessResponse> listBusinessesForUser() {
        UserEntity user = currentUserService.resolveAuthenticatedUser();

        return businessRepository.findByUsers_IdAndIsDeletedFalseOrderByIdAsc(user.getId()).stream()
                .map(BusinessService::toBusinessResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessResponse getBusiness(Long businessId) {
        BusinessEntity business = businessRepository
                .findByIdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
        return toBusinessResponse(business);
    }

    @Transactional
    public BusinessResponse updateBusiness(Long businessId, BusinessUpdateRequest request) {
        if (request == null
                || (request.name() == null
                        && request.email() == null
                        && request.businessType() == null
                        && request.description() == null
                        && request.address() == null
                        && request.mobileNumber() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        BusinessEntity business = businessRepository
                .findByIdAndIsDeletedFalse(businessId)
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
        if (request.businessType() != null) {
            business.setBusinessType(InputSanitizer.normalizeName(request.businessType()));
        }
        if (request.description() != null) {
            business.setDescription(InputSanitizer.trimToNull(request.description()));
        }
        if (request.address() != null) {
            business.setAddress(InputSanitizer.trimToNull(request.address()));
        }
        if (request.mobileNumber() != null) {
            String mobile = InputSanitizer.trimToNull(request.mobileNumber());
            if (mobile != null) {
                mobile = InputSanitizer.normalizeMobile(mobile);
                businessRepository
                        .findByMobileNumber(mobile)
                        .filter(other -> !other.getId().equals(businessId))
                        .ifPresent(other -> {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already in use");
                        });
            }
            business.setMobileNumber(mobile);
        }

        business = businessRepository.save(business);
        return toBusinessResponse(business);
    }

    private static BusinessResponse toBusinessResponse(BusinessEntity business) {
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getEmail(),
                business.getApiKey(),
                business.getIsDeleted(),
                business.getBusinessType(),
                business.getDescription(),
                Boolean.TRUE.equals(business.getIsVerified()),
                business.getAverageRating() != null ? business.getAverageRating() : 0.0,
                business.getAddress(),
                business.getMobileNumber());
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
                .findByIdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        if (businessConfigRepository.existsByBusiness_IdAndIsDeletedFalse(businessId)) {
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
                .findByIdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        // PaymentChannelEntity paymentChannel = paymentChannelService.findById(request.paymentChannelId());

        if (businessPaymentChannelConfigRepository.existsByBusiness_IdAndIsDeletedFalse(businessId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Business already has an payment channel configuration");
        }

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
        if (!businessRepository.findByIdAndIsDeletedFalse(businessId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found");
        }
        return businessPaymentChannelConfigRepository
                .findByBusiness_IdAndIsDeletedFalseOrderByIdAsc(businessId).stream()
                .map(BusinessService::toBusinessPaymentChannelConfigResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessPaymentChannelConfigResponse getBusinessPaymentChannelConfig(Long businessId, Long configId) {
        BusinessPaymentChannelConfigEntity entity = businessPaymentChannelConfigRepository
                .findByIdAndBusiness_IdAndIsDeletedFalse(configId, businessId)
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
                .findByIdAndBusiness_IdAndIsDeletedFalse(configId, businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business payment channel config not found"));
        config.setIsDeleted(true);
        businessPaymentChannelConfigRepository.save(config);
    }
}
