package com.digirestro.digi_payment_gateway.controller.ui;

import com.digirestro.digi_payment_gateway.dto.MerchantPaymentChannelConfigCreateRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantPaymentChannelConfigResponse;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationResponse;
import com.digirestro.digi_payment_gateway.service.MerchantService;
import jakarta.validation.Valid;
import java.util.Map;
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
@RequestMapping("/api/v1/ui/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    // TODO:  authenticated user via JWT - Security checks for all endpoints - user is mapped to merchant then only allow to modifty merchant settings else show you are not authorized to access this resource

    // Merchants
    @PostMapping
    public ResponseEntity<MerchantRegistrationResponse> createMerchant(
            @Valid @RequestBody MerchantRegistrationRequest request) {
        MerchantRegistrationResponse response = merchantService.createMerchant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<String> listMerchants() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("List merchants endpoint is not implemented yet.");
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<String> getMerchant(@PathVariable("merchantId") Long merchantId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Get merchant endpoint is not implemented yet.");
    }

    @PatchMapping("/{merchantId}")
    public ResponseEntity<String> updateMerchant(
            @PathVariable("merchantId") Long merchantId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Update merchant endpoint is not implemented yet.");
    }

    // implement userid check in future - user is mapped to merchant
    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable("merchantId") Long merchantId) {
        merchantService.deactivateMerchant(merchantId);
        return ResponseEntity.noContent().build();
    }

    // Merchant payment channel configs
    @PostMapping("/{merchantId}/payment-channel-configs")
    public ResponseEntity<MerchantPaymentChannelConfigResponse> createMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @Valid @RequestBody MerchantPaymentChannelConfigCreateRequest request) {
        MerchantPaymentChannelConfigResponse response = merchantService.createMerchantPaymentChannelConfig(merchantId,
                request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{merchantId}/payment-channel-configs")
    public ResponseEntity<String> listMerchantPaymentChannelConfigs(@PathVariable("merchantId") Long merchantId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("List merchant payment channel configs endpoint is not implemented yet.");
    }

    @GetMapping("/{merchantId}/payment-channel-configs/{configId}")
    public ResponseEntity<String> getMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @PathVariable("configId") Long configId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Get merchant payment channel config endpoint is not implemented yet.");
    }

    @PatchMapping("/{merchantId}/payment-channel-configs/{configId}")
    public ResponseEntity<String> updateMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @PathVariable("configId") Long configId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Update merchant payment channel config endpoint is not implemented yet.");
    }

    // implement userid check in future - user is mapped to merchant
    @DeleteMapping("/{merchantId}/payment-channel-configs/{configId}")
    public ResponseEntity<Void> deleteMerchantPaymentChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @PathVariable("configId") Long configId) {
        merchantService.deactivateMerchantPaymentChannelConfig(merchantId, configId);
        return ResponseEntity.noContent().build();
    }

}
