package com.runalb.ondemand_service.catalog.dto;

public record CatalogCategoryResponse(
        Long id, String name, String description, String imageUrl, Integer displayOrder) {}
