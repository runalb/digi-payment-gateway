package com.runalb.ondemand_service.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CatalogCategoryCreateRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description,
        Integer displayOrder,
        Boolean active) {}
