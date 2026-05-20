package com.runalb.ondemand_service.offering.controller;

import com.runalb.ondemand_service.offering.dto.CatalogBusinessOfferingResponse;
import com.runalb.ondemand_service.offering.service.CatalogServiceOfferingService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog/services")
public class CatalogServiceOfferingController {

    private final CatalogServiceOfferingService catalogServiceOfferingService;

    public CatalogServiceOfferingController(CatalogServiceOfferingService catalogServiceOfferingService) {
        this.catalogServiceOfferingService = catalogServiceOfferingService;
    }

    @GetMapping("/{serviceId}/business-offerings")
    public ResponseEntity<List<CatalogBusinessOfferingResponse>> listActiveAndVerifiedBusinessOfferingsForService(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(
                catalogServiceOfferingService.listActiveAndVerifiedBusinessOfferingsForService(serviceId));
    }
}
