package com.runalb.ondemand_service.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLogoutRequest(@NotBlank String refreshToken) {}
