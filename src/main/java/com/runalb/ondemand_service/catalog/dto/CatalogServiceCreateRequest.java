package com.runalb.ondemand_service.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CatalogServiceCreateRequest(
        @NotBlank @Size(max = 512) String name,
        @Size(max = 4000) String description,
        List<@Size(max = 2048) String> imageUrls,
        Integer displayOrder) {}
