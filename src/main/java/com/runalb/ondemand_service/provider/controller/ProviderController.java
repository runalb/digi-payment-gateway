package com.runalb.ondemand_service.provider.controller;

import com.runalb.ondemand_service.auth.service.AuthService;
import com.runalb.ondemand_service.provider.dto.ProviderCreateRequest;
import com.runalb.ondemand_service.provider.dto.ProviderDetailResponse;
import com.runalb.ondemand_service.provider.dto.ProviderUpdateRequest;
import com.runalb.ondemand_service.provider.service.ProviderService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping
    public ResponseEntity<List<ProviderDetailResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderDetailResponse> getProvider(@PathVariable("providerId") Long providerId) {
        return ResponseEntity.ok(providerService.getProvider(providerId));
    }

    @PostMapping
    public ResponseEntity<ProviderDetailResponse> createProvider(@Valid @RequestBody ProviderCreateRequest request) {
        ProviderDetailResponse body = providerService.createProvider(request);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PutMapping("/{providerId}")
    public ResponseEntity<ProviderDetailResponse> updateProvider(
            @PathVariable("providerId") Long providerId, @Valid @RequestBody ProviderUpdateRequest request) {
        authService.assertAuthenticatedUserOwnsProvider(providerId);
        return ResponseEntity.ok(providerService.updateProvider(providerId, request));
    }

    @DeleteMapping("/{providerId}")
    public ResponseEntity<Void> deleteProvider(@PathVariable("providerId") Long providerId) {
        authService.assertAuthenticatedUserOwnsProvider(providerId);
        providerService.deleteProvider(providerId);
        return ResponseEntity.noContent().build();
    }

}
