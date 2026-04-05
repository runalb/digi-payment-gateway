package com.digirestro.digi_payment_gateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthEmailOtpRequest(
        @NotBlank @Email @Size(max = 255) String email) {}
