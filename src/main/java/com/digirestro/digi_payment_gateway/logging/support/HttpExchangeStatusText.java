package com.digirestro.digi_payment_gateway.logging.support;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public final class HttpExchangeStatusText {

    private HttpExchangeStatusText() {}

    public static String resolve(Integer responseStatus, String responseStatusText, Throwable error) {
        if (StringUtils.hasText(responseStatusText)) {
            return responseStatusText.trim();
        }
        if (responseStatus != null) {
            HttpStatus httpStatus = HttpStatus.resolve(responseStatus);
            if (httpStatus != null) {
                return httpStatus.getReasonPhrase();
            }
        }
        if (error != null) {
            return "Failed";
        }
        return null;
    }
}
