package com.runalb.ondemand_service.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CatalogServiceCreateRequest(
        @NotBlank @Size(max = 512) String name,
        @Size(max = 4000) String description,
        Integer displayOrder,
        Boolean active) {}
