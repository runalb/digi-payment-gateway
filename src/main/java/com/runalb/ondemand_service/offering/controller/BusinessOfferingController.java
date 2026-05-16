package com.runalb.ondemand_service.offering.controller;

import com.runalb.ondemand_service.security.AuthorizationService;
import com.runalb.ondemand_service.offering.dto.BusinessOfferingLinkRequest;
import com.runalb.ondemand_service.offering.dto.BusinessOfferingResponse;
import com.runalb.ondemand_service.offering.dto.BusinessOfferingUpdateRequest;
import com.runalb.ondemand_service.offering.service.BusinessOfferingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/offerings")
public class BusinessOfferingController {

    private final BusinessOfferingService businessOfferingService;
    private final AuthorizationService authorizationService;

    public BusinessOfferingController(
            BusinessOfferingService businessOfferingService, AuthorizationService authorizationService) {
        this.businessOfferingService = businessOfferingService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<List<BusinessOfferingResponse>> linkOfferings(
            @PathVariable Long businessId, @Valid @RequestBody BusinessOfferingLinkRequest request) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        List<BusinessOfferingResponse> response = businessOfferingService.linkOfferings(businessId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BusinessOfferingResponse>> listOfferings(@PathVariable Long businessId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        return ResponseEntity.ok(businessOfferingService.listOfferingsForBusiness(businessId));
    }

    @PatchMapping("/{offeringId}")
    public ResponseEntity<BusinessOfferingResponse> updateOfferingActiveStatus(
            @PathVariable Long businessId,
            @PathVariable Long offeringId,
            @Valid @RequestBody BusinessOfferingUpdateRequest request) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessOfferingResponse response =
                businessOfferingService.updateOfferingActiveStatus(businessId, offeringId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{offeringId}")
    public ResponseEntity<Void> unlinkOffering(@PathVariable Long businessId, @PathVariable Long offeringId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        businessOfferingService.unlinkOffering(businessId, offeringId);
        return ResponseEntity.noContent().build();
    }
}
