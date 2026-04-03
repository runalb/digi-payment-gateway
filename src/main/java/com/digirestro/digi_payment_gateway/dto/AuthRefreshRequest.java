package com.digirestro.digi_payment_gateway.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRefreshRequest(@NotBlank String refreshToken) {}
