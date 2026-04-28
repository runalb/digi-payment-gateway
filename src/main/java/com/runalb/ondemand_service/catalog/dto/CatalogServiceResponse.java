package com.runalb.ondemand_service.catalog.dto;

import java.time.LocalDateTime;

public record CatalogServiceResponse(
        Long id,
        String name,
        String description,
        Integer displayOrder,
        Boolean active,
        CatalogCategoryResponse category,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
