package com.runalb.ondemand_service.offering.dto;

import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;

public record CatalogBusinessOfferingResponse(
        Long offeringId,
        Long businessId,
        String businessName,
        Double businessAverageRating,
        Boolean isActive,
        CatalogServiceResponse catalogService) {}
