package com.digirestro.digi_payment_gateway.logging.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public final class HttpExchangeContentType {

    private HttpExchangeContentType() {}

    public static String fromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String header = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.hasText(header)) {
            return header.trim();
        }
        return trimToNull(request.getContentType());
    }

    public static String fromResponse(HttpServletResponse response) {
        if (response == null) {
            return null;
        }
        return trimToNull(response.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    public static String fromHeaders(HttpHeaders headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return trimToNull(headers.getFirst(HttpHeaders.CONTENT_TYPE));
    }

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
