package com.digirestro.digi_payment_gateway.logging.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class HttpExchangeLogAsyncConfig {

    @Bean(name = "httpExchangeLogExecutor")
    Executor httpExchangeLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("http-exchange-log-");
        executor.initialize();
        return executor;
    }
}
