package com.digirestro.digi_payment_gateway.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLogoutRequest(@NotBlank String refreshToken) {}
