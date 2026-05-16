package com.runalb.ondemand_service.offering.controller;

import com.runalb.ondemand_service.offering.service.CatalogServiceOfferingService;
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

    @GetMapping("/{serviceId}/offerings")
    public ResponseEntity<Void> listActiveOfferingsForService(@PathVariable Long serviceId) {
        catalogServiceOfferingService.listActiveOfferingsForService(serviceId);
        return ResponseEntity.ok().build();
    }
}
