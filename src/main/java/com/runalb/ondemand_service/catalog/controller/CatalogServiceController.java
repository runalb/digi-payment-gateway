package com.runalb.ondemand_service.catalog.controller;

import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceUpdateRequest;
import com.runalb.ondemand_service.catalog.service.CatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog/services")
public class CatalogServiceController {

    private final CatalogService catalogService;

    public CatalogServiceController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<List<CatalogServiceResponse>> listAllServices() {
        return ResponseEntity.ok(catalogService.listAllCatalogServices());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CatalogServiceResponse>> searchServices(@RequestParam String q) {
        return ResponseEntity.ok(catalogService.searchCatalogServices(q));
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<CatalogServiceResponse> getService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(catalogService.getCatalogService(serviceId));
    }

    @PatchMapping("/{serviceId}")
    public ResponseEntity<CatalogServiceResponse> updateService(
            @PathVariable Long serviceId, @Valid @RequestBody CatalogServiceUpdateRequest request) {
        return ResponseEntity.ok(catalogService.updateCatalogService(serviceId, request));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId) {
        catalogService.deleteCatalogService(serviceId);
        return ResponseEntity.noContent().build();
    }
}
