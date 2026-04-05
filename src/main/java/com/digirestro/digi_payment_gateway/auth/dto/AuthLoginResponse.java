package com.digirestro.digi_payment_gateway.auth.dto;

public record AuthLoginResponse(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        String refreshToken,
        Long refreshExpiresInSeconds) {}
