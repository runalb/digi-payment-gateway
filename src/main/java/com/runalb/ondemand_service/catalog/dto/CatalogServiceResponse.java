package com.runalb.ondemand_service.catalog.dto;

import java.util.List;

public record CatalogServiceResponse(
        Long id,
        String name,
        String description,
        Integer displayOrder,
        List<CatalogServiceImageResponse> images,
        CatalogCategoryResponse category) {}
