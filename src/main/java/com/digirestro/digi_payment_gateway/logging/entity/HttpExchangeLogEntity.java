package com.digirestro.digi_payment_gateway.logging.entity;

import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeDirectionEnum;
import com.digirestro.digi_payment_gateway.logging.enums.HttpExchangeOutcomeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "http_exchange_log")
public class HttpExchangeLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id", nullable = false, length = 64)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private HttpExchangeDirectionEnum direction;

    @Column(name = "http_method", nullable = false, length = 16)
    private String httpMethod;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "request_headers", columnDefinition = "TEXT")
    private String requestHeaders;

    @Column(name = "request_content_type", length = 255)
    private String requestContentType;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "response_status_text", length = 64)
    private String responseStatusText;

    @Column(name = "response_headers", columnDefinition = "TEXT")
    private String responseHeaders;

    @Column(name = "response_content_type", length = 255)
    private String responseContentType;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private HttpExchangeOutcomeEnum outcome;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "error_detail", columnDefinition = "TEXT")
    private String errorDetail;
}
