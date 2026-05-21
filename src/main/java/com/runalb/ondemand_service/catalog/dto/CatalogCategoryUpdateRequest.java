package com.runalb.ondemand_service.catalog.dto;

import jakarta.validation.constraints.Size;

public record CatalogCategoryUpdateRequest(
        @Size(max = 255) String name,
        @Size(max = 2000) String description,
        @Size(max = 2048) String imageUrl,
        Integer displayOrder) {}
