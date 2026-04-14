package com.digirestro.digi_payment_gateway.portal.merchant.dto.merchantconfig;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MerchantConfigCreateRequest(
        @NotBlank
        @Size(min = 3, max = 3)
        @Pattern(regexp = "[A-Za-z]{3}", message = "currency must be a 3-letter ISO 4217 code")
        String currency,
        String webhookUrl) {}
