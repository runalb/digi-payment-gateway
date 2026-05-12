package com.runalb.ondemand_service.provider.controller;

import com.runalb.ondemand_service.auth.service.AuthService;
import com.runalb.ondemand_service.provider.dto.ProviderCreateRequest;
import com.runalb.ondemand_service.provider.dto.ProviderDetailResponse;
import com.runalb.ondemand_service.provider.dto.ProviderUpdateRequest;
import com.runalb.ondemand_service.provider.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {

    private final ProviderService providerService;
    private final AuthService authService;

    public ProviderController(ProviderService providerService, AuthService authService) {
        this.providerService = providerService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ProviderDetailResponse> createProvider(@Valid @RequestBody ProviderCreateRequest request) {
        authService.assertAuthenticatedUserHasProviderRole();
        ProviderDetailResponse body = providerService.createProfile(request);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PutMapping("/{providerId}")
    public ResponseEntity<ProviderDetailResponse> updateProvider(
            @PathVariable("providerId") Long providerId, @Valid @RequestBody ProviderUpdateRequest request) {
        authService.assertAuthenticatedUserHasProviderRole();
        return ResponseEntity.ok(providerService.updateProfile(providerId, request));
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderDetailResponse> getProvider(@PathVariable("providerId") Long providerId) {
        authService.assertAuthenticatedUserHasProviderRole();
        return ResponseEntity.ok(providerService.getProfileForOwner(providerId));
    }
}
