package com.runalb.ondemand_service.catalog.dto;

import jakarta.validation.constraints.Size;

public record CatalogServiceUpdateRequest(
        @Size(max = 512) String name,
        @Size(max = 4000) String description,
        Integer displayOrder,
        Boolean active,
        Long categoryId) {}
