package com.runalb.ondemand_service.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

/** Validates and normalizes catalog image URL input. */
public final class ImageUrlValidator {

    public static final int MAX_URL_LENGTH = 2048;

    private ImageUrlValidator() {}

    /** Returns null when blank; otherwise a trimmed, validated URL. */
    public static String normalizeImageUrl(String value) {
        String trimmed = InputSanitizer.trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        validateUrl(trimmed);
        return trimmed;
    }

    /**
     * Normalizes a non-null image URL list: trims, validates, deduplicates (stable order), and enforces max count.
     * An empty list is allowed.
     */
    public static List<String> normalizeImageUrlList(List<String> imageUrls, int maxImages) {
        if (imageUrls == null) {
            return null;
        }
        if (imageUrls.isEmpty()) {
            return List.of();
        }
        if (imageUrls.size() > maxImages) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "At most " + maxImages + " images are allowed per service");
        }
        Set<String> seen = new LinkedHashSet<>();
        List<String> result = new ArrayList<>();
        for (String raw : imageUrls) {
            if (!StringUtils.hasText(raw)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imageUrls must not contain blank entries");
            }
            String url = normalizeImageUrl(raw);
            if (seen.add(url)) {
                result.add(url);
            }
        }
        if (result.size() > maxImages) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "At most " + maxImages + " images are allowed per service");
        }
        return List.copyOf(result);
    }

    private static void validateUrl(String url) {
        if (url.length() > MAX_URL_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "image URL must be at most " + MAX_URL_LENGTH + " characters");
        }
        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image URL");
        }
        String scheme = uri.getScheme();
        if (scheme == null
                || (!"https".equalsIgnoreCase(scheme) && !"http".equalsIgnoreCase(scheme))
                || uri.getHost() == null
                || uri.getHost().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "image URL must be a valid http or https URL with a host");
        }
    }
}
