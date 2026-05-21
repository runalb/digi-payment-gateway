package com.runalb.ondemand_service.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.repository.CatalogCategoryRepository;
import com.runalb.ondemand_service.catalog.repository.CatalogServiceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CatalogServiceSearchTest {

    @Mock
    private CatalogCategoryRepository categoryRepository;

    @Mock
    private CatalogServiceRepository catalogServiceRepository;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogService(categoryRepository, catalogServiceRepository, 5);
    }

    @Test
    void searchCatalogServices_blankQueryRejected() {
        assertThrows(ResponseStatusException.class, () -> catalogService.searchCatalogServices("  "));
    }

    @Test
    void searchCatalogServices_matchesByNameKeyword() {
        CatalogCategoryEntity category = category(1L, "Beauty");
        CatalogServiceEntity hair = service(10L, "Hair Cut", category);
        when(catalogServiceRepository.findByIsDeletedFalseAndNameContainingIgnoreCase(eq("hair"), any(Sort.class)))
                .thenReturn(List.of(hair));

        List<CatalogServiceResponse> result = catalogService.searchCatalogServices("  hair ");

        assertEquals(1, result.size());
        assertEquals("Hair Cut", result.getFirst().name());
    }

    @Test
    void searchServicesInCategory_blankQueryRejected() {
        assertThrows(ResponseStatusException.class, () -> catalogService.searchServicesInCategory(2L, "  "));
    }

    @Test
    void searchServicesInCategory_matchesWithinCategory() {
        CatalogCategoryEntity category = category(3L, "Cleaning");
        CatalogServiceEntity match = service(20L, "Deep Home Cleaning", category);
        when(categoryRepository.findByIdAndIsDeletedFalse(3L)).thenReturn(Optional.of(category));
        when(catalogServiceRepository
                        .findByCatalogCategory_IdAndIsDeletedFalseAndNameContainingIgnoreCaseOrderByDisplayOrderAscIdAsc(
                                3L, "clean"))
                .thenReturn(List.of(match));

        List<CatalogServiceResponse> result = catalogService.searchServicesInCategory(3L, "clean");

        assertEquals(1, result.size());
        assertEquals("Deep Home Cleaning", result.getFirst().name());
        assertEquals(3L, result.getFirst().category().id());
    }

    private static CatalogCategoryEntity category(long id, String name) {
        CatalogCategoryEntity category = new CatalogCategoryEntity();
        category.setId(id);
        category.setName(name);
        return category;
    }

    private static CatalogServiceEntity service(long id, String name, CatalogCategoryEntity category) {
        CatalogServiceEntity service = new CatalogServiceEntity();
        service.setId(id);
        service.setName(name);
        service.setCatalogCategory(category);
        service.setDisplayOrder(0);
        return service;
    }
}
