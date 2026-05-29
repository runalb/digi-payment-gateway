package com.digirestro.digi_payment_gateway.logging.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HttpExchangeLogProperties.class)
public class HttpExchangeLogConfig {}
