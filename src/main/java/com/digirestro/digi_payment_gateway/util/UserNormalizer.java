package com.digirestro.digi_payment_gateway.util;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

public final class UserNormalizer {

    private static final Pattern WHITESPACE_RUN = Pattern.compile("\\s+");

    private UserNormalizer() {}

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

    public static String normalizeName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        return WHITESPACE_RUN.matcher(trimmed).replaceAll(" ");
    }
}
