package com.runalb.ondemand_service.business.service;

import com.runalb.ondemand_service.business.dto.BusinessCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.mapper.BusinessDtoMapper;
import com.runalb.ondemand_service.business.dto.BusinessUpdateRequest;
import com.runalb.ondemand_service.business.entity.BusinessConfigEntity;
import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.business.entity.BusinessPaymentChannelConfigEntity;
import com.runalb.ondemand_service.business.repository.BusinessConfigRepository;
import com.runalb.ondemand_service.business.repository.BusinessPaymentChannelConfigRepository;
import com.runalb.ondemand_service.business.repository.BusinessRepository;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.security.CurrentUserService;
import com.runalb.ondemand_service.relationship.service.EntityLinkService;
import com.runalb.ondemand_service.util.InputSanitizer;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final EntityLinkService entityLinkService;
    private final CurrentUserService currentUserService;

    public BusinessService(
            BusinessRepository businessRepository,
            EntityLinkService entityLinkService,
            CurrentUserService currentUserService) {
        this.businessRepository = businessRepository;
        this.entityLinkService = entityLinkService;
        this.currentUserService = currentUserService;
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
        business.setEmail(email);
        business.setBusinessType(InputSanitizer.normalizeName(request.businessType()));
        business.setDescription(InputSanitizer.trimToNull(request.description()));
        business.setAddress(InputSanitizer.trimToNull(request.address()));
        business.setMobileNumber(mobileNumber);
        business.setIsVerified(Boolean.FALSE);
        business.setAverageRating(0.0);
        business = businessRepository.save(business);

        entityLinkService.linkUserToBusiness(user, business);

        return BusinessDtoMapper.toResponse(business);
    }

    @Transactional(readOnly = true)
    public List<BusinessResponse> listBusinessesForUser() {
        UserEntity user = currentUserService.resolveAuthenticatedUser();

        return businessRepository.findByUsers_IdAndIsDeletedFalseOrderByIdAsc(user.getId()).stream()
                .map(BusinessDtoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessResponse getBusiness(Long businessId) {
        BusinessEntity business = businessRepository
                .findByIdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
        return BusinessDtoMapper.toResponse(business);
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
        return BusinessDtoMapper.toResponse(business);
    }

    @Transactional
    public void deactivateBusiness(Long businessId) {
        BusinessEntity business = businessRepository
                .findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
        business.setIsDeleted(true);
        businessRepository.save(business);
    }

}
