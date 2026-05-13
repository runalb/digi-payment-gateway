package com.runalb.ondemand_service.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.runalb.ondemand_service.business.entity.BusinessEntity;

@Service
public class IntegrationAuthService {

    public BusinessEntity extractBusiness(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing integration authentication");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof BusinessEntity business)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid integration principal");
        }
        if (business.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid integration principal");
        }
        return business;
    }
}
