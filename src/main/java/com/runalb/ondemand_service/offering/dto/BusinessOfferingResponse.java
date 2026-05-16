package com.runalb.ondemand_service.offering.dto;

import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;

public record BusinessOfferingResponse(
        Long id, Long businessId, Boolean isActive, CatalogServiceResponse catalogService) {}
