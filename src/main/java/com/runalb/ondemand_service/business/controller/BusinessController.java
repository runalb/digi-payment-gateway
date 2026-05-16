package com.runalb.ondemand_service.business.controller;

import com.runalb.ondemand_service.security.AuthorizationService;
import com.runalb.ondemand_service.business.dto.BusinessConfigCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessConfigResponse;
import com.runalb.ondemand_service.business.dto.BusinessConfigUpdateRequest;
import com.runalb.ondemand_service.business.dto.BusinessCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessPaymentChannelConfigCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessPaymentChannelConfigResponse;
import com.runalb.ondemand_service.business.dto.BusinessPaymentChannelConfigUpdateRequest;
import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.dto.BusinessUpdateRequest;
import com.runalb.ondemand_service.business.service.BusinessService;

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

// Note - Not used in this project
@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {

    private final BusinessService businessService;
    private final AuthorizationService authorizationService;

    public BusinessController(BusinessService businessService, AuthorizationService authorizationService) {
        this.businessService = businessService;
        this.authorizationService = authorizationService;
    }

    // Businesses
    @PostMapping
    public ResponseEntity<BusinessResponse> createBusiness(
            @Valid @RequestBody BusinessCreateRequest request) {
        BusinessResponse response = businessService.createBusiness(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BusinessResponse>> listBusinessesForUser() {
        List<BusinessResponse> businesses = businessService.listBusinessesForUser();
        return new ResponseEntity<>(businesses, HttpStatus.OK);
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<BusinessResponse> getBusiness(@PathVariable("businessId") Long businessId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessResponse response = businessService.getBusiness(businessId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{businessId}")
    public ResponseEntity<BusinessResponse> updateBusiness(
            @PathVariable("businessId") Long businessId,
            @Valid @RequestBody BusinessUpdateRequest request) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessResponse response = businessService.updateBusiness(businessId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{businessId}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable("businessId") Long businessId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        businessService.deactivateBusiness(businessId);
        return ResponseEntity.noContent().build();
    }

    // Business configuration
    @GetMapping("/{businessId}/config")
    public ResponseEntity<BusinessConfigResponse> getBusinessConfig(@PathVariable("businessId") Long businessId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessConfigResponse response = businessService.getBusinessConfig(businessId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{businessId}/config")
    public ResponseEntity<BusinessConfigResponse> createBusinessConfig(
            @PathVariable("businessId") Long businessId,
            @Valid @RequestBody BusinessConfigCreateRequest request) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessConfigResponse response = businessService.createBusinessConfig(businessId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{businessId}/config")
    public ResponseEntity<BusinessConfigResponse> updateBusinessConfig(
            @PathVariable("businessId") Long businessId,
            @Valid @RequestBody BusinessConfigUpdateRequest request) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessConfigResponse response = businessService.updateBusinessConfig(businessId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{businessId}/config")
    public ResponseEntity<Void> deleteBusinessConfig(@PathVariable("businessId") Long businessId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        businessService.deactivateBusinessConfig(businessId);
        return ResponseEntity.noContent().build();
    }

    // Business payment channel configs
    @PostMapping("/{businessId}/payment-channel-configs")
    public ResponseEntity<BusinessPaymentChannelConfigResponse> createBusinessPaymentChannelConfig(
            @PathVariable("businessId") Long businessId,
            @Valid @RequestBody BusinessPaymentChannelConfigCreateRequest request) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessPaymentChannelConfigResponse response = businessService.createBusinessPaymentChannelConfig(businessId,
                request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{businessId}/payment-channel-configs")
    public ResponseEntity<List<BusinessPaymentChannelConfigResponse>> listBusinessPaymentChannelConfigs(
            @PathVariable("businessId") Long businessId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        List<BusinessPaymentChannelConfigResponse> response =
                businessService.listBusinessPaymentChannelConfigs(businessId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{businessId}/payment-channel-configs/{configId}")
    public ResponseEntity<BusinessPaymentChannelConfigResponse> getBusinessPaymentChannelConfig(
            @PathVariable("businessId") Long businessId,
            @PathVariable("configId") Long configId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessPaymentChannelConfigResponse response =
                businessService.getBusinessPaymentChannelConfig(businessId, configId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{businessId}/payment-channel-configs/{configId}")
    public ResponseEntity<BusinessPaymentChannelConfigResponse> updateBusinessPaymentChannelConfig(
            @PathVariable("businessId") Long businessId,
            @PathVariable("configId") Long configId,
            @Valid @RequestBody BusinessPaymentChannelConfigUpdateRequest request) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        BusinessPaymentChannelConfigResponse response =
                businessService.updateBusinessPaymentChannelConfig(businessId, configId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{businessId}/payment-channel-configs/{configId}")
    public ResponseEntity<Void> deleteBusinessPaymentChannelConfig(
            @PathVariable("businessId") Long businessId,
            @PathVariable("configId") Long configId) {
        authorizationService.assertAuthenticatedUserOwnsBusiness(businessId);
        businessService.deactivateBusinessPaymentChannelConfig(businessId, configId);
        return ResponseEntity.noContent().build();
    }

}
