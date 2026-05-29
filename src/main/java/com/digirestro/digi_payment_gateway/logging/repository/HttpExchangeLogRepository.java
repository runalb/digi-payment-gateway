package com.digirestro.digi_payment_gateway.logging.repository;

import com.digirestro.digi_payment_gateway.logging.entity.HttpExchangeLogEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HttpExchangeLogRepository extends JpaRepository<HttpExchangeLogEntity, Long> {

    List<HttpExchangeLogEntity> findByCorrelationIdOrderByStartedAtAsc(String correlationId);
}
