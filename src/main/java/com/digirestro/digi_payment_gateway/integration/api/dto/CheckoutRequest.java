package com.digirestro.digi_payment_gateway.integration.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CheckoutRequest(
        @NotBlank(message = "merchantReferenceId is required") String merchantReferenceId,
        @NotNull(message = "amount is required") @DecimalMin(value = "0.01", message = "amount must be at least 0.01") BigDecimal amount,
        String redirectSuccessUrl,
        String redirectFailureUrl

        // TODO: Add json key and value pairs to merchantMetadataJson
        // String merchantMetadataJson
        ) {}
