package com.runalb.ondemand_service.catalog.dto;

public record CatalogServiceResponse(
        Long id,
        String name,
        String description,
        Integer displayOrder,
        CatalogCategoryResponse category
) {}
