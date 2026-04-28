package com.runalb.ondemand_service.business.controller;

import com.runalb.ondemand_service.auth.service.AuthService;
import com.runalb.ondemand_service.business.dto.BusinessCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.dto.BusinessUpdateRequest;
import com.runalb.ondemand_service.business.service.BusinessService;
import com.runalb.ondemand_service.user.entity.UserEntity;

import jakarta.validation.Valid;

import java.util.ArrayList;
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
@RequestMapping("/api/v1/business")
public class BusinessController {

    private final BusinessService businessService;
    private final AuthService authService;

    public BusinessController(BusinessService businessService, AuthService authService) {
        this.businessService = businessService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<BusinessResponse> createBusiness(
            @Valid @RequestBody BusinessCreateRequest request) {
        //TODO: only providers can create businesses
        UserEntity user = authService.loadAuthenticatedActiveUser();
        BusinessResponse response = businessService.createBusiness(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BusinessResponse>> listMerchants() {
        // TODO: implement this
        // Long userId = authService.loadAuthenticatedActiveUser().getId();
        // List<BusinessResponse> businesses = businessService.listBusinessesForUser(userId);
        List<BusinessResponse> businesses = new ArrayList<>();
        return new ResponseEntity<>(businesses, HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<?> getBusiness(@PathVariable("businessId") Long businessId) {
        // TODO: implement this
        // authService.assertAuthenticatedUserOwnsBusiness(businessId);
        // BusinessResponse response = businessService.getBusiness(businessId);
        // return new ResponseEntity<>(response, HttpStatus.OK);
        return new ResponseEntity<>("Not implemented", HttpStatus.NOT_IMPLEMENTED);
    }

    @PatchMapping("/{businessId}")
    public ResponseEntity<?> updateBusiness(
            @PathVariable("businessId") Long businessId,
            @Valid @RequestBody BusinessUpdateRequest request) {
        // TODO: implement this
        // authService.assertAuthenticatedUserOwnsBusiness(businessId);
        // BusinessResponse response = businessService.updateBusiness(businessId, request);
        return new ResponseEntity<>("Not implemented", HttpStatus.NOT_IMPLEMENTED);
    }

    @DeleteMapping("/{businessId}")
    public ResponseEntity<?> deleteBusiness(@PathVariable("businessId") Long businessId) {
        // TODO: implement this
        // authService.assertAuthenticatedUserOwnsBusiness(businessId);
        // businessService.deactivateMerchant(merchantId);
        return new ResponseEntity<>("Not implemented", HttpStatus.NOT_IMPLEMENTED);
    }

}
