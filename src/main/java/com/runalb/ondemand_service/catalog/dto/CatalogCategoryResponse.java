package com.runalb.ondemand_service.catalog.dto;

import java.time.LocalDateTime;

public record CatalogCategoryResponse(
        Long id,
        String name,
        String description,
        Integer displayOrder,
        Boolean active,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
