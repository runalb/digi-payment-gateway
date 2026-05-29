package com.digirestro.digi_payment_gateway.logging.support;

public final class HttpExchangeCorrelation {

    public static final String HEADER_NAME = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    private HttpExchangeCorrelation() {}

    public static String currentOrNull() {
        return org.slf4j.MDC.get(MDC_KEY);
    }
}
