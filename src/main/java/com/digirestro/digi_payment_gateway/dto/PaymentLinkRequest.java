package com.digirestro.digi_payment_gateway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentLinkRequest(
        @NotNull Long merchantId,
        @NotBlank String merchantReferencePaymentId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String merchantMetadataJson) {}
