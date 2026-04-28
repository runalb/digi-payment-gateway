package com.runalb.ondemand_service.auth.dto;

public record AuthLoginResponse(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        String refreshToken,
        Long refreshExpiresInSeconds) {}
