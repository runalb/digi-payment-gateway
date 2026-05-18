package com.runalb.ondemand_service.offering.controller;

import com.runalb.ondemand_service.offering.service.CatalogOfferingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogOfferingController {

    private final CatalogOfferingService catalogOfferingService;

    public CatalogOfferingController(CatalogOfferingService catalogOfferingService) {
        this.catalogOfferingService = catalogOfferingService;
    }

    @GetMapping("/services/{serviceId}/offerings")
    public ResponseEntity<Void> listActiveAndVerifiedOfferingsForService(@PathVariable Long serviceId) {
        catalogOfferingService.listActiveAndVerifiedOfferingsForService(serviceId);
        return ResponseEntity.ok().build();
    }
}
