package com.digirestro.digi_payment_gateway.controller.ui;

import com.digirestro.digi_payment_gateway.dto.MerchantChannelConfigCreateRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantChannelConfigResponse;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationResponse;
import com.digirestro.digi_payment_gateway.service.MerchantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<MerchantRegistrationResponse> register(@Valid @RequestBody MerchantRegistrationRequest request) {
        MerchantRegistrationResponse response = merchantService.registerMerchant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{merchantId}/channel-configs")
    public ResponseEntity<MerchantChannelConfigResponse> createChannelConfig(
            @PathVariable("merchantId") Long merchantId,
            @Valid @RequestBody MerchantChannelConfigCreateRequest request) {
        MerchantChannelConfigResponse response = merchantService.createMerchantChannelConfig(merchantId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
