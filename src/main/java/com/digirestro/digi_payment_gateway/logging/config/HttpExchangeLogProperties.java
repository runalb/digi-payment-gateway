package com.digirestro.digi_payment_gateway.logging.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "http.exchange.log")
public class HttpExchangeLogProperties {

    private boolean enabled = true;
    private int maxBodySize = 65_536;
    private boolean logRequestHeaders = true;
    private boolean logResponseHeaders = false;
    private boolean includeErrorDetail = true;
    private int maxErrorDetailLength = 8_192;
    private List<String> excludePaths = new ArrayList<>(List.of("/actuator/**", "/test-checkout.html/**"));
}
