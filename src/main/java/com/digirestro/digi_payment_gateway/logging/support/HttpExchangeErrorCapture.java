package com.digirestro.digi_payment_gateway.logging.support;

import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeOutcomeEnum;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public final class HttpExchangeErrorCapture {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private HttpExchangeErrorCapture() {}

    public record NormalizedExchange(
            Throwable failure,
            Integer responseStatus,
            String responseStatusText,
            String responseBody) {}

    public record ErrorFields(String message, String detail) {
        static ErrorFields empty() {
            return new ErrorFields(null, null);
        }
    }

    public static NormalizedExchange normalize(
            Throwable failure, Integer responseStatus, String responseStatusText, String responseBody) {
        if (failure instanceof HttpStatusCodeException statusException) {
            String errorBody = responseBody;
            if (!StringUtils.hasText(errorBody)) {
                errorBody = statusException.getResponseBodyAsString(StandardCharsets.UTF_8);
            }
            return new NormalizedExchange(
                    null,
                    statusException.getStatusCode().value(),
                    statusException.getStatusText(),
                    errorBody);
        }
        return new NormalizedExchange(failure, responseStatus, responseStatusText, responseBody);
    }

    public static ErrorFields capture(
            HttpExchangeOutcomeEnum outcome,
            NormalizedExchange exchange,
            boolean includeFailureStackTrace) {
        if (outcome == HttpExchangeOutcomeEnum.SUCCESS) {
            return ErrorFields.empty();
        }

        if (exchange.failure() != null) {
            return new ErrorFields(
                    exchange.failure().getMessage(),
                    includeFailureStackTrace ? toStackTrace(exchange.failure()) : null);
        }

        String message = extractMessageFromBody(exchange.responseBody());
        if (!StringUtils.hasText(message)) {
            message = formatStatusMessage(exchange.responseStatus(), exchange.responseStatusText());
        }

        String detail = StringUtils.hasText(exchange.responseBody()) ? exchange.responseBody() : message;
        return new ErrorFields(message, detail);
    }

    private static String extractMessageFromBody(String responseBody) {
        if (!StringUtils.hasText(responseBody)) {
            return null;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            JsonNode messageNode = root.get("message");
            if (messageNode != null && !messageNode.isNull() && messageNode.isValueNode()) {
                return messageNode.asString();
            }
            JsonNode errorNode = root.get("error");
            if (errorNode != null && !errorNode.isNull() && errorNode.isValueNode()) {
                return errorNode.asString();
            }
        } catch (Exception ignored) {
            // not JSON — use raw body as message below if needed
        }
        return responseBody.trim();
    }

    private static String formatStatusMessage(Integer responseStatus, String responseStatusText) {
        if (responseStatus == null) {
            return "Request failed";
        }
        if (StringUtils.hasText(responseStatusText)) {
            return responseStatus + " " + responseStatusText.trim();
        }
        HttpStatus httpStatus = HttpStatus.resolve(responseStatus);
        if (httpStatus != null) {
            return responseStatus + " " + httpStatus.getReasonPhrase();
        }
        return String.valueOf(responseStatus);
    }

    private static String toStackTrace(Throwable failure) {
        StringWriter writer = new StringWriter();
        failure.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
