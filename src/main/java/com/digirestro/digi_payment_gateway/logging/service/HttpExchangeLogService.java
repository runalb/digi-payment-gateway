package com.digirestro.digi_payment_gateway.logging.service;

import com.digirestro.digi_payment_gateway.logging.config.HttpExchangeLogProperties;
import com.digirestro.digi_payment_gateway.logging.dto.HttpExchangeLogEntry;
import com.digirestro.digi_payment_gateway.logging.entity.HttpExchangeLogEntity;
import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeOutcomeEnum;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeErrorCapture;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeErrorCapture.NormalizedExchange;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HttpExchangeLogService {

    private final HttpExchangeLogAsyncWriter asyncWriter;
    private final HttpExchangeLogProperties properties;

    public HttpExchangeLogService(
            HttpExchangeLogAsyncWriter asyncWriter, HttpExchangeLogProperties properties) {
        this.asyncWriter = asyncWriter;
        this.properties = properties;
    }

    public void record(HttpExchangeLogEntry entry) {
        if (!properties.isEnabled()) {
            return;
        }
        asyncWriter.persist(toEntity(entry));
    }

    public static HttpExchangeOutcomeEnum resolveOutcome(Integer responseStatus, Throwable error) {
        NormalizedExchange normalized = HttpExchangeErrorCapture.normalize(error, responseStatus, null, null);
        error = normalized.failure();
        responseStatus = normalized.responseStatus();

        if (error != null) {
            return HttpExchangeOutcomeEnum.FAILED;
        }
        if (responseStatus == null) {
            return HttpExchangeOutcomeEnum.FAILED;
        }
        if (responseStatus >= 500) {
            return HttpExchangeOutcomeEnum.SERVER_ERROR;
        }
        if (responseStatus >= 400) {
            return HttpExchangeOutcomeEnum.CLIENT_ERROR;
        }
        return HttpExchangeOutcomeEnum.SUCCESS;
    }

    private HttpExchangeLogEntity toEntity(HttpExchangeLogEntry entry) {
        HttpExchangeLogEntity entity = new HttpExchangeLogEntity();
        entity.setCorrelationId(entry.getCorrelationId());
        entity.setDirection(entry.getDirection());
        entity.setHttpMethod(entry.getHttpMethod());
        entity.setUrl(truncate(entry.getUrl(), properties.getMaxBodySize()));
        entity.setStartedAt(entry.getStartedAt());
        entity.setCompletedAt(entry.getCompletedAt());
        entity.setRequestHeaders(
                properties.isLogRequestHeaders()
                        ? truncate(entry.getRequestHeaders(), properties.getMaxBodySize())
                        : null);
        entity.setRequestContentType(entry.getRequestContentType());
        entity.setRequestBody(truncate(entry.getRequestBody(), properties.getMaxBodySize()));
        entity.setResponseStatus(entry.getResponseStatus());
        entity.setResponseStatusText(entry.getResponseStatusText());
        entity.setResponseHeaders(
                properties.isLogResponseHeaders()
                        ? truncate(entry.getResponseHeaders(), properties.getMaxBodySize())
                        : null);
        entity.setResponseContentType(entry.getResponseContentType());
        entity.setResponseBody(truncate(entry.getResponseBody(), properties.getMaxBodySize()));
        entity.setDurationMs(entry.getDurationMs());
        entity.setOutcome(entry.getOutcome());
        entity.setErrorMessage(truncate(entry.getErrorMessage(), properties.getMaxBodySize()));
        entity.setErrorDetail(
                entry.getErrorDetail() == null
                        ? null
                        : truncate(entry.getErrorDetail(), properties.getMaxErrorDetailLength()));
        return entity;
    }

    private static String truncate(String value, int maxLength) {
        if (!StringUtils.hasText(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...[truncated]";
    }
}
