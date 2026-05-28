package com.digirestro.digi_payment_gateway.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.error("Handled IllegalArgumentException on path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        log.error("Handled EntityNotFoundException on path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnhandledException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unhandled exception on path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleUnhandledThrowable(
            Throwable ex,
            HttpServletRequest request) {
        log.error("Unhandled throwable on path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String message,
            String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message == null ? "" : message);
        body.put("path", path);
        return new ResponseEntity<>(body, status);
    }
}
