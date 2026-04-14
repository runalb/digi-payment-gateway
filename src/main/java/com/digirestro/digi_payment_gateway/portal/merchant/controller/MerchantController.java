package com.digirestro.digi_payment_gateway.portal.merchant.controller;

import com.digirestro.digi_payment_gateway.auth.service.AuthService;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchant.MerchantCreateRequest;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchant.MerchantResponse;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchant.MerchantUpdateRequest;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchantconfig.MerchantConfigCreateRequest;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchantconfig.MerchantConfigResponse;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchantconfig.MerchantConfigUpdateRequest;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchantpaymentchannel.MerchantPaymentChannelConfigCreateRequest;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchantpaymentchannel.MerchantPaymentChannelConfigResponse;
import com.digirestro.digi_payment_gateway.portal.merchant.dto.merchantpaymentchannel.MerchantPaymentChannelConfigUpdateRequest;
import com.digirestro.digi_payment_gateway.portal.merchant.service.MerchantService;

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
@RequestMapping("/api/v1/portal/merchants")
public class MerchantController {

    private final MerchantService merchantService;
    private final AuthService authService;

    public MerchantController(MerchantService merchantService, AuthService authService) {
        this.merchantService = merchantService;
        this.authService = authService;
    }

    // Merchants
    @PostMapping
    public ResponseEntity<MerchantResponse> createMerchant(
            @Valid @RequestBody MerchantCreateRequest request) {
        Long ownerUserId = authService.loadAuthenticatedActiveUser().getId();
        MerchantResponse response = merchantService.createMerchant(request, ownerUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MerchantResponse>> listMerchants() {
        Long userId = authService.loadAuthenticatedActiveUser().getId();
        List<MerchantResponse> merchants = merchantService.listMerchantsForUser(userId);
        return new ResponseEntity<>(merchants, HttpStatus.OK);
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponse> getMerchant(@PathVariable("merchantId") Long merchantId) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantResponse response = merchantService.getMerchant(merchantId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{merchantId}")
    public ResponseEntity<MerchantResponse> updateMerchant(
            @PathVariable("merchantId") Long merchantId,
            @Valid @RequestBody MerchantUpdateRequest request) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantResponse response = merchantService.updateMerchant(merchantId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable("merchantId") Long merchantId) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        merchantService.deactivateMerchant(merchantId);
        return ResponseEntity.noContent().build();
    }

    // Merchant configuration
    @GetMapping("/{merchantId}/config")
    public ResponseEntity<MerchantConfigResponse> getMerchantConfig(@PathVariable("merchantId") Long merchantId) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantConfigResponse response = merchantService.getMerchantConfig(merchantId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{merchantId}/config")
    public ResponseEntity<MerchantConfigResponse> createMerchantConfig(
            @PathVariable("merchantId") Long merchantId,
            @Valid @RequestBody MerchantConfigCreateRequest request) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantConfigResponse response = merchantService.createMerchantConfig(merchantId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{merchantId}/config")
    public ResponseEntity<MerchantConfigResponse> updateMerchantConfig(
            @PathVariable("merchantId") Long merchantId,
            @Valid @RequestBody MerchantConfigUpdateRequest request) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantConfigResponse response = merchantService.updateMerchantConfig(merchantId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Merchant payment channel configs
    @PostMapping("/{merchantId}/payment-channel-configs")
    public ResponseEntity<MerchantPaymentChannelConfigResponse> createMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @Valid @RequestBody MerchantPaymentChannelConfigCreateRequest request) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantPaymentChannelConfigResponse response = merchantService.createMerchantPaymentChannelConfig(merchantId,
                request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{merchantId}/payment-channel-configs")
    public ResponseEntity<List<MerchantPaymentChannelConfigResponse>> listMerchantPaymentChannelConfigs(
            @PathVariable("merchantId") Long merchantId) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        List<MerchantPaymentChannelConfigResponse> response =
                merchantService.listMerchantPaymentChannelConfigs(merchantId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{merchantId}/payment-channel-configs/{configId}")
    public ResponseEntity<MerchantPaymentChannelConfigResponse> getMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @PathVariable("configId") Long configId) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantPaymentChannelConfigResponse response =
                merchantService.getMerchantPaymentChannelConfig(merchantId, configId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{merchantId}/payment-channel-configs/{configId}")
    public ResponseEntity<MerchantPaymentChannelConfigResponse> updateMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @PathVariable("configId") Long configId,
            @Valid @RequestBody MerchantPaymentChannelConfigUpdateRequest request) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        MerchantPaymentChannelConfigResponse response =
                merchantService.updateMerchantPaymentChannelConfig(merchantId, configId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{merchantId}/payment-channel-configs/{configId}")
    public ResponseEntity<Void> deleteMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @PathVariable("configId") Long configId) {
        authService.assertAuthenticatedUserOwnsMerchant(merchantId);
        merchantService.deactivateMerchantPaymentChannelConfig(merchantId, configId);
        return ResponseEntity.noContent().build();
    }

}
