package com.runalb.ondemand_service.catalog.controller;

import com.runalb.ondemand_service.catalog.dto.CatalogServiceCreateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceUpdateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryCreateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryUpdateRequest;
import com.runalb.ondemand_service.catalog.service.CatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CatalogCategoryResponse>> listCategories() {
        return ResponseEntity.ok(catalogService.listCategories());
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CatalogCategoryResponse> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(catalogService.getCategory(categoryId));
    }

    @PostMapping("/categories")
    public ResponseEntity<CatalogCategoryResponse> createCategory(
            @Valid @RequestBody CatalogCategoryCreateRequest request) {
        CatalogCategoryResponse body = catalogService.createCategory(request);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<CatalogCategoryResponse> updateCategory(
            @PathVariable Long categoryId, @Valid @RequestBody CatalogCategoryUpdateRequest request) {
        return ResponseEntity.ok(catalogService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        catalogService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories/{categoryId}/services")
    public ResponseEntity<List<CatalogServiceResponse>> listServices(@PathVariable Long categoryId) {
        return ResponseEntity.ok(catalogService.listServicesInCategory(categoryId));
    }

    @PostMapping("/categories/{categoryId}/services")
    public ResponseEntity<CatalogServiceResponse> createService(
            @PathVariable Long categoryId, @Valid @RequestBody CatalogServiceCreateRequest request) {
        CatalogServiceResponse body = catalogService.createService(categoryId, request);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<CatalogServiceResponse> getService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(catalogService.getCatalogService(serviceId));
    }

    @PatchMapping("/services/{serviceId}")
    public ResponseEntity<CatalogServiceResponse> updateService(
            @PathVariable Long serviceId, @Valid @RequestBody CatalogServiceUpdateRequest request) {
        return ResponseEntity.ok(catalogService.updateCatalogService(serviceId, request));
    }

    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId) {
        catalogService.deleteCatalogService(serviceId);
        return ResponseEntity.noContent().build();
    }
}
