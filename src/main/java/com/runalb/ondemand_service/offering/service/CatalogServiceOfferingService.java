package com.runalb.ondemand_service.offering.service;

import com.runalb.ondemand_service.catalog.dto.CatalogCategoryResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.repository.CatalogServiceRepository;
import com.runalb.ondemand_service.offering.dto.CatalogBusinessOfferingResponse;
import com.runalb.ondemand_service.offering.entity.BusinessOfferingEntity;
import com.runalb.ondemand_service.offering.repository.BusinessOfferingRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogServiceOfferingService {

    private final CatalogServiceRepository catalogServiceRepository;
    private final BusinessOfferingRepository businessOfferingRepository;

    public CatalogServiceOfferingService(
            CatalogServiceRepository catalogServiceRepository,
            BusinessOfferingRepository businessOfferingRepository) {
        this.catalogServiceRepository = catalogServiceRepository;
        this.businessOfferingRepository = businessOfferingRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogBusinessOfferingResponse> listActiveAndVerifiedBusinessOfferingsForService(
            Long serviceId) {
        catalogServiceRepository
                .findWithCatalogCategoryByIdAndIsDeletedFalse(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog service not found"));

        return businessOfferingRepository
                .findByCatalogService_IdAndIsDeletedFalseAndIsActiveTrueAndIsVerifiedTrueOrderByIdAsc(serviceId)
                .stream()
                .map(CatalogServiceOfferingService::toCatalogBusinessOfferingResponse)
                .toList();
    }

    private static CatalogBusinessOfferingResponse toCatalogBusinessOfferingResponse(
            BusinessOfferingEntity offering) {
        return new CatalogBusinessOfferingResponse(
                offering.getId(),
                offering.getBusiness().getId(),
                offering.getBusiness().getName(),
                offering.getBusiness().getAverageRating(),
                Boolean.TRUE.equals(offering.getIsActive()),
                toCatalogServiceResponse(offering.getCatalogService()));
    }

    private static CatalogServiceResponse toCatalogServiceResponse(CatalogServiceEntity service) {
        return new CatalogServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDisplayOrder(),
                toCatalogCategoryResponse(service.getCatalogCategory()));
    }

    private static CatalogCategoryResponse toCatalogCategoryResponse(CatalogCategoryEntity category) {
        return new CatalogCategoryResponse(
                category.getId(), category.getName(), category.getDescription(), category.getDisplayOrder());
    }
}
