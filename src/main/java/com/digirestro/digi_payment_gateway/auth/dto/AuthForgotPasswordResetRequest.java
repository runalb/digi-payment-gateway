package com.digirestro.digi_payment_gateway.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthForgotPasswordResetRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Pattern(regexp = "^\\d{6}$", message = "otp must be 6 digits") String otp,
        @NotBlank @Size(min = 8, max = 128) String newPassword) {}
