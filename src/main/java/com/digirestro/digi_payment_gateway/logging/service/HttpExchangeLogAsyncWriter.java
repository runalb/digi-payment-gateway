package com.digirestro.digi_payment_gateway.logging.service;

import com.digirestro.digi_payment_gateway.logging.entity.HttpExchangeLogEntity;
import com.digirestro.digi_payment_gateway.logging.repository.HttpExchangeLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HttpExchangeLogAsyncWriter {

    private final HttpExchangeLogRepository repository;

    public HttpExchangeLogAsyncWriter(HttpExchangeLogRepository repository) {
        this.repository = repository;
    }

    @Async("httpExchangeLogExecutor")
    public void persist(HttpExchangeLogEntity entity) {
        try {
            repository.save(entity);
        } catch (Exception ex) {
            log.warn(
                    "Failed to persist http exchange log for correlationId={}, direction={}, url={}",
                    entity.getCorrelationId(),
                    entity.getDirection(),
                    entity.getUrl(),
                    ex);
        }
    }
}
