package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.entity.MerchantEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IntegrationAuthenticationService {

    public MerchantEntity extractMerchant(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing integration authentication");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof MerchantEntity merchant)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid integration principal");
        }
        if (merchant.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid integration principal");
        }
        return merchant;
    }
}
