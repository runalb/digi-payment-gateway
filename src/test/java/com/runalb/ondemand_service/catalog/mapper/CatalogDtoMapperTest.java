package com.runalb.ondemand_service.catalog.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.runalb.ondemand_service.catalog.dto.CatalogCategoryResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceImageResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceImageEntity;
import java.util.List;
import org.junit.jupiter.api.Test;

class CatalogDtoMapperTest {

    @Test
    void toCategoryResponse_mapsAllFields() {
        CatalogCategoryEntity category = new CatalogCategoryEntity();
        category.setId(1L);
        category.setName("Cleaning");
        category.setDescription("Home services");
        category.setImageUrl("https://cdn.example.com/cat.jpg");
        category.setDisplayOrder(2);

        CatalogCategoryResponse response = CatalogDtoMapper.toCategoryResponse(category);

        assertEquals(1L, response.id());
        assertEquals("Cleaning", response.name());
        assertEquals("Home services", response.description());
        assertEquals("https://cdn.example.com/cat.jpg", response.imageUrl());
        assertEquals(2, response.displayOrder());
    }

    @Test
    void toImageResponses_returnsEmptyListWhenNullOrEmpty() {
        assertTrue(CatalogDtoMapper.toImageResponses(null).isEmpty());
        assertTrue(CatalogDtoMapper.toImageResponses(List.of()).isEmpty());
    }

    @Test
    void toImageResponses_mapsOrderedImages() {
        CatalogServiceImageEntity first = image(10L, "https://cdn.example.com/a.jpg", 0);
        CatalogServiceImageEntity second = image(11L, "https://cdn.example.com/b.jpg", 1);

        List<CatalogServiceImageResponse> responses =
                CatalogDtoMapper.toImageResponses(List.of(first, second));

        assertEquals(2, responses.size());
        assertEquals(10L, responses.get(0).id());
        assertEquals("https://cdn.example.com/a.jpg", responses.get(0).imageUrl());
        assertEquals(0, responses.get(0).displayOrder());
        assertEquals(11L, responses.get(1).id());
        assertEquals("https://cdn.example.com/b.jpg", responses.get(1).imageUrl());
        assertEquals(1, responses.get(1).displayOrder());
    }

    @Test
    void toServiceResponse_mapsServiceCategoryAndImages() {
        CatalogCategoryEntity category = new CatalogCategoryEntity();
        category.setId(5L);
        category.setName("Plumbing");
        category.setDescription(null);
        category.setImageUrl(null);
        category.setDisplayOrder(0);

        CatalogServiceEntity service = new CatalogServiceEntity();
        service.setId(20L);
        service.setName("Pipe repair");
        service.setDescription("Emergency");
        service.setDisplayOrder(1);
        service.setCatalogCategory(category);
        service.setImages(List.of(image(30L, "https://cdn.example.com/s.jpg", 0)));

        CatalogServiceResponse response = CatalogDtoMapper.toServiceResponse(service);

        assertEquals(20L, response.id());
        assertEquals("Pipe repair", response.name());
        assertEquals("Emergency", response.description());
        assertEquals(1, response.displayOrder());
        assertEquals(1, response.images().size());
        assertEquals(30L, response.images().get(0).id());
        assertEquals(5L, response.category().id());
        assertEquals("Plumbing", response.category().name());
    }

    private static CatalogServiceImageEntity image(Long id, String url, int displayOrder) {
        CatalogServiceImageEntity image = new CatalogServiceImageEntity();
        image.setId(id);
        image.setImageUrl(url);
        image.setDisplayOrder(displayOrder);
        return image;
    }
}
