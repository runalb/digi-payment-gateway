package com.runalb.ondemand_service.util;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

/** Trims, validates, and normalizes user-supplied string input. */
public final class InputSanitizer {

    private static final Pattern WHITESPACE_RUN = Pattern.compile("\\s+");
    private static final Pattern ISO_4217_CURRENCY = Pattern.compile("[A-Za-z]{3}");

    private InputSanitizer() {}

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

    /** Returns null when blank after trim; otherwise trimmed text with collapsed whitespace. */
    public static String normalizeSearchQuery(String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        String normalized = WHITESPACE_RUN.matcher(query.trim()).replaceAll(" ");
        return normalized.isEmpty() ? null : normalized;
    }

    /** Returns null when the value is blank; otherwise returns the trimmed string. */
    public static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static boolean hasText(String value) {
        return StringUtils.hasText(value);
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
