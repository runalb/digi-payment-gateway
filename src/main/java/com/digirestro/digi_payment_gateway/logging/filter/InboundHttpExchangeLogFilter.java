package com.digirestro.digi_payment_gateway.logging.filter;

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
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class InboundHttpExchangeLogFilter extends OncePerRequestFilter {

    private final HttpExchangeLogService logService;
    private final HttpExchangeLogProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public InboundHttpExchangeLogFilter(
            HttpExchangeLogService logService, HttpExchangeLogProperties properties) {
        this.logService = logService;
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return true;
        }
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        for (String pattern : properties.getExcludePaths()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);
        MDC.put(HttpExchangeCorrelation.MDC_KEY, correlationId);
        response.setHeader(HttpExchangeCorrelation.HEADER_NAME, correlationId);

        int cacheLimit = properties.getMaxBodySize();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, cacheLimit);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        LocalDateTime startedAt = LocalDateTime.now();
        long startedAtMillis = System.currentTimeMillis();
        Throwable failure = null;
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Throwable ex) {
            failure = ex;
            throw ex;
        } finally {
            try {
                LocalDateTime completedAt = LocalDateTime.now();
                long durationMs = System.currentTimeMillis() - startedAtMillis;
                Charset charset = resolveCharset(wrappedRequest.getCharacterEncoding());
                String requestBody = readBody(wrappedRequest.getContentAsByteArray(), charset);
                String responseBody = readBody(wrappedResponse.getContentAsByteArray(), charset);

                Integer responseStatus = wrappedResponse.getStatus();
                NormalizedExchange exchange =
                        HttpExchangeErrorCapture.normalize(failure, responseStatus, null, responseBody);
                HttpExchangeOutcomeEnum outcome =
                        HttpExchangeLogService.resolveOutcome(exchange.responseStatus(), exchange.failure());
                String responseStatusText =
                        HttpExchangeStatusText.resolve(
                                exchange.responseStatus(), null, exchange.failure());
                ErrorFields errorFields =
                        HttpExchangeErrorCapture.capture(
                                outcome, exchange, properties.isIncludeErrorDetail());

                logService.record(
                        HttpExchangeLogEntry.builder()
                                .correlationId(correlationId)
                                .direction(HttpExchangeDirectionEnum.INBOUND)
                                .httpMethod(request.getMethod())
                                .url(buildInboundUrl(request))
                                .startedAt(startedAt)
                                .completedAt(completedAt)
                                .requestHeaders(formatRequestHeaders(request))
                                .requestContentType(HttpExchangeContentType.fromRequest(request))
                                .requestBody(requestBody)
                                .responseStatus(exchange.responseStatus())
                                .responseStatusText(responseStatusText)
                                .responseHeaders(formatResponseHeaders(wrappedResponse))
                                .responseContentType(HttpExchangeContentType.fromResponse(wrappedResponse))
                                .responseBody(responseBody)
                                .durationMs(durationMs)
                                .outcome(outcome)
                                .errorMessage(errorFields.message())
                                .errorDetail(errorFields.detail())
                                .build());
                wrappedResponse.copyBodyToResponse();
            } finally {
                MDC.remove(HttpExchangeCorrelation.MDC_KEY);
            }
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String incoming = request.getHeader(HttpExchangeCorrelation.HEADER_NAME);
        if (StringUtils.hasText(incoming)) {
            return incoming.trim();
        }
        return UUID.randomUUID().toString();
    }

    private static String buildInboundUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        if (!StringUtils.hasText(query)) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + '?' + query;
    }

    private static String formatRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return null;
        }
        return Collections.list(headerNames).stream()
                .map(name -> name + ": " + request.getHeader(name))
                .collect(Collectors.joining("\n"));
    }

    private static String formatResponseHeaders(HttpServletResponse response) {
        Collection<String> headerNames = response.getHeaderNames();
        if (headerNames == null || headerNames.isEmpty()) {
            return null;
        }
        return headerNames.stream()
                .map(name -> name + ": " + response.getHeader(name))
                .collect(Collectors.joining("\n"));
    }

    private static String readBody(byte[] content, Charset charset) {
        if (content == null || content.length == 0) {
            return null;
        }
        return new String(content, charset);
    }

    private static Charset resolveCharset(String encoding) {
        if (!StringUtils.hasText(encoding)) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(encoding);
    }
}
