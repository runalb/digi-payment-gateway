package com.digirestro.digi_payment_gateway.logging.dto;

import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeDirectionEnum;
import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeOutcomeEnum;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HttpExchangeLogEntry {

    private final String correlationId;
    private final HttpExchangeDirectionEnum direction;
    private final String httpMethod;
    private final String url;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final String requestHeaders;
    private final String requestContentType;
    private final String requestBody;
    private final Integer responseStatus;
    private final String responseStatusText;
    private final String responseHeaders;
    private final String responseContentType;
    private final String responseBody;
    private final Long durationMs;
    private final HttpExchangeOutcomeEnum outcome;
    private final String errorMessage;
    private final String errorDetail;
}
