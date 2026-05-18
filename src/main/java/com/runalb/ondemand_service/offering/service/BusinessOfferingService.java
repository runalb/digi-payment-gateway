package com.runalb.ondemand_service.offering.service;

import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.business.repository.BusinessRepository;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.repository.CatalogServiceRepository;
import com.runalb.ondemand_service.offering.dto.BusinessOfferingLinkRequest;
import com.runalb.ondemand_service.offering.dto.BusinessOfferingResponse;
import com.runalb.ondemand_service.offering.dto.BusinessOfferingUpdateRequest;
import com.runalb.ondemand_service.offering.entity.BusinessOfferingEntity;
import com.runalb.ondemand_service.offering.repository.BusinessOfferingRepository;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BusinessOfferingService {

    private final BusinessRepository businessRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final BusinessOfferingRepository businessOfferingRepository;

    public BusinessOfferingService(
            BusinessRepository businessRepository,
            CatalogServiceRepository catalogServiceRepository,
            BusinessOfferingRepository businessOfferingRepository) {
        this.businessRepository = businessRepository;
        this.catalogServiceRepository = catalogServiceRepository;
        this.businessOfferingRepository = businessOfferingRepository;
    }

    @Transactional
    public List<BusinessOfferingResponse> linkOfferings(Long businessId, BusinessOfferingLinkRequest request) {
        BusinessEntity business = requireActiveBusiness(businessId);

        Set<Long> uniqueServiceIds = new LinkedHashSet<>(request.catalogServiceIds());
        List<BusinessOfferingResponse> linked = new ArrayList<>();

        for (Long catalogServiceId : uniqueServiceIds) {
            CatalogServiceEntity catalogService = catalogServiceRepository
                    .findWithCatalogCategoryByIdAndIsDeletedFalse(catalogServiceId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Catalog service not found: " + catalogServiceId));

            BusinessOfferingEntity offering = businessOfferingRepository
                    .findByBusiness_IdAndCatalogService_Id(businessId, catalogServiceId)
                    .orElseGet(BusinessOfferingEntity::new);

            if (offering.getId() != null && Boolean.FALSE.equals(offering.getIsDeleted())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Business already offers catalog service: " + catalogServiceId);
            }

            offering.setBusiness(business);
            offering.setCatalogService(catalogService);
            offering.setIsDeleted(Boolean.FALSE);
            offering.setIsActive(Boolean.TRUE);
            offering.setIsVerified(Boolean.FALSE);
            offering = businessOfferingRepository.save(offering);
            linked.add(toBusinessOfferingResponse(offering));
        }

        return linked;
    }

    @Transactional(readOnly = true)
    public List<BusinessOfferingResponse> listOfferingsForBusiness(Long businessId) {
        requireActiveBusiness(businessId);
        return businessOfferingRepository.findByBusiness_IdAndIsDeletedFalseOrderByIdAsc(businessId).stream()
                .map(BusinessOfferingService::toBusinessOfferingResponse)
                .toList();
    }

    @Transactional
    public BusinessOfferingResponse updateOfferingActiveStatus(
            Long businessId, Long offeringId, BusinessOfferingUpdateRequest request) {
        requireActiveBusiness(businessId);
        BusinessOfferingEntity offering = businessOfferingRepository
                .findByIdAndBusiness_IdAndIsDeletedFalse(offeringId, businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found"));
        offering.setIsActive(request.isActive());
        offering = businessOfferingRepository.save(offering);
        return toBusinessOfferingResponse(offering);
    }

    @Transactional
    public BusinessOfferingResponse verifyOffering(Long businessId, Long offeringId) {
        requireActiveBusiness(businessId);
        BusinessOfferingEntity offering = businessOfferingRepository
                .findByIdAndBusiness_IdAndIsDeletedFalse(offeringId, businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found"));
        if (Boolean.TRUE.equals(offering.getIsVerified())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Offering already verified");
        }
        offering.setIsVerified(Boolean.TRUE);
        offering = businessOfferingRepository.save(offering);
        return toBusinessOfferingResponse(offering);
    }

    @Transactional
    public void unlinkOffering(Long businessId, Long offeringId) {
        requireActiveBusiness(businessId);
        BusinessOfferingEntity offering = businessOfferingRepository
                .findByIdAndBusiness_IdAndIsDeletedFalse(offeringId, businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found"));
        offering.setIsDeleted(Boolean.TRUE);
        businessOfferingRepository.save(offering);
    }

    private BusinessEntity requireActiveBusiness(Long businessId) {
        return businessRepository
                .findByIdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
    }

    private static BusinessOfferingResponse toBusinessOfferingResponse(BusinessOfferingEntity offering) {
        return new BusinessOfferingResponse(
                offering.getId(),
                offering.getBusiness().getId(),
                Boolean.TRUE.equals(offering.getIsActive()),
                Boolean.TRUE.equals(offering.getIsVerified()),
                toCatalogServiceResponse(offering.getCatalogService()));
    }

    private static CatalogServiceResponse toCatalogServiceResponse(CatalogServiceEntity service) {
        return new CatalogServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDisplayOrder(),
                toCatalogCategoryResponse(service.getCatalogCategory()),
                service.getCreatedDateTime(),
                service.getUpdatedDateTime());
    }

    private static CatalogCategoryResponse toCatalogCategoryResponse(CatalogCategoryEntity category) {
        return new CatalogCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getDisplayOrder(),
                category.getCreatedDateTime(),
                category.getUpdatedDateTime());
    }
}
