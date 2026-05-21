package com.runalb.ondemand_service.catalog.mapper;

import com.runalb.ondemand_service.catalog.dto.CatalogCategoryResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceImageResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceImageEntity;
import java.util.ArrayList;
import java.util.List;

public final class CatalogDtoMapper {

    private CatalogDtoMapper() {}

    public static CatalogCategoryResponse toCategoryResponse(CatalogCategoryEntity category) {
        return new CatalogCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl(),
                category.getDisplayOrder());
    }

    public static CatalogServiceResponse toServiceResponse(CatalogServiceEntity service) {
        return new CatalogServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDisplayOrder(),
                toImageResponses(service.getImages()),
                toCategoryResponse(service.getCatalogCategory()));
    }

    public static List<CatalogServiceImageResponse> toImageResponses(List<CatalogServiceImageEntity> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        List<CatalogServiceImageResponse> result = new ArrayList<>(images.size());
        for (CatalogServiceImageEntity image : images) {
            result.add(new CatalogServiceImageResponse(
                    image.getId(), image.getImageUrl(), image.getDisplayOrder()));
        }
        return List.copyOf(result);
    }
}
