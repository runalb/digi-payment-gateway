package com.digirestro.digi_payment_gateway.logging.client;

import com.digirestro.digi_payment_gateway.logging.config.HttpExchangeLogProperties;
import com.digirestro.digi_payment_gateway.logging.dto.HttpExchangeLogEntry;
import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeDirectionEnum;
import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeOutcomeEnum;
import com.digirestro.digi_payment_gateway.logging.service.HttpExchangeLogService;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeContentType;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeCorrelation;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeErrorCapture;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeErrorCapture.ErrorFields;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeErrorCapture.NormalizedExchange;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeStatusText;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class OutboundHttpExchangeLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final HttpExchangeLogService logService;
    private final HttpExchangeLogProperties properties;

    public OutboundHttpExchangeLoggingInterceptor(
            HttpExchangeLogService logService, HttpExchangeLogProperties properties) {
        this.logService = logService;
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (!properties.isEnabled()) {
            return execution.execute(request, body);
        }

        LocalDateTime startedAt = LocalDateTime.now();
        long startedAtMillis = System.currentTimeMillis();
        String correlationId = resolveCorrelationId();
        Throwable failure = null;
        ClientHttpResponse response = null;
        byte[] responseBody = null;

        try {
            response = execution.execute(request, body);
            responseBody = StreamUtils.copyToByteArray(response.getBody());
            return new BufferingClientHttpResponseWrapper(response, responseBody);
        } catch (Throwable ex) {
            failure = ex;
            throw ex;
        } finally {
            LocalDateTime completedAt = LocalDateTime.now();
            long durationMs = System.currentTimeMillis() - startedAtMillis;
            Integer responseStatus = response == null ? null : response.getStatusCode().value();
            String responseStatusText = null;
            if (response != null) {
                try {
                    responseStatusText = response.getStatusText();
                } catch (IOException ignored) {
                    // fall back to status code mapping in HttpExchangeStatusText
                }
            }
            String responseBodyText =
                    responseBody == null ? null : new String(responseBody, StandardCharsets.UTF_8);
            String responseContentType = resolveResponseContentType(response, failure);
            NormalizedExchange exchange = HttpExchangeErrorCapture.normalize(
                    failure, responseStatus, responseStatusText, responseBodyText);
            HttpExchangeOutcomeEnum outcome =
                    HttpExchangeLogService.resolveOutcome(exchange.responseStatus(), exchange.failure());
            String resolvedStatusText =
                    HttpExchangeStatusText.resolve(
                            exchange.responseStatus(), exchange.responseStatusText(), exchange.failure());
            ErrorFields errorFields =
                    HttpExchangeErrorCapture.capture(
                            outcome, exchange, properties.isIncludeErrorDetail());

            logService.record(
                    HttpExchangeLogEntry.builder()
                            .correlationId(correlationId)
                            .direction(HttpExchangeDirectionEnum.OUTBOUND)
                            .httpMethod(request.getMethod().name())
                            .url(request.getURI().toString())
                            .startedAt(startedAt)
                            .completedAt(completedAt)
                            .requestHeaders(formatHeaders(request))
                            .requestContentType(HttpExchangeContentType.fromHeaders(request.getHeaders()))
                            .requestBody(readBody(body))
                            .responseStatus(exchange.responseStatus())
                            .responseStatusText(resolvedStatusText)
                            .responseHeaders(response == null ? null : formatHeaders(response))
                            .responseContentType(responseContentType)
                            .responseBody(exchange.responseBody())
                            .durationMs(durationMs)
                            .outcome(outcome)
                            .errorMessage(errorFields.message())
                            .errorDetail(errorFields.detail())
                            .build());
        }
    }

    private static String resolveResponseContentType(ClientHttpResponse response, Throwable failure)
            throws IOException {
        if (response != null) {
            return HttpExchangeContentType.fromHeaders(response.getHeaders());
        }
        if (failure instanceof HttpStatusCodeException statusException) {
            return HttpExchangeContentType.fromHeaders(statusException.getResponseHeaders());
        }
        return null;
    }

    private String resolveCorrelationId() {
        String fromMdc = MDC.get(HttpExchangeCorrelation.MDC_KEY);
        if (StringUtils.hasText(fromMdc)) {
            return fromMdc;
        }
        return java.util.UUID.randomUUID().toString();
    }

    private static String formatHeaders(HttpRequest request) {
        return formatHeaders(request.getHeaders());
    }

    private static String formatHeaders(ClientHttpResponse response) throws IOException {
        return formatHeaders(response.getHeaders());
    }

    private static String formatHeaders(HttpHeaders headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        headers.forEach((name, values) -> {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(name).append(": ").append(String.join(", ", values));
        });
        return builder.toString();
    }

    private static String readBody(byte[] body) {
        if (body == null || body.length == 0) {
            return null;
        }
        return new String(body, StandardCharsets.UTF_8);
    }
}
