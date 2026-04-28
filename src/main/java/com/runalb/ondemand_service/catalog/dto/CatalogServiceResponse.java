package com.runalb.ondemand_service.catalog.dto;

import java.time.LocalDateTime;

public record CatalogServiceResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        Integer displayOrder,
        Boolean active,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
