package com.digirestro.digi_payment_gateway.util;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

public final class ContactNormalizer {

    private ContactNormalizer() {}

    public static String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }
        return email.trim().toLowerCase();
    }

    public static String normalizeMobile(String mobileNumber) {
        if (!StringUtils.hasText(mobileNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mobileNumber is required");
        }
        return mobileNumber.trim();
    }
}
