package com.runalb.ondemand_service.util;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

public final class StringNormalizer {

    private static final Pattern WHITESPACE_RUN = Pattern.compile("\\s+");
    private static final Pattern ISO_4217_CURRENCY = Pattern.compile("[A-Za-z]{3}");

    private StringNormalizer() {}

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

    public static String normalizeISO4217Currency(String currency) {
        if (!StringUtils.hasText(currency)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currency is required");
        }
        String trimmed = currency.trim();
        if (!ISO_4217_CURRENCY.matcher(trimmed).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "currency must be a 3-letter ISO 4217 code");
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }
}
