package com.digirestro.digi_payment_gateway.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthMobileOtpRequest(
        @NotBlank
                @Pattern(
                        regexp = "^\\+?[1-9]\\d{7,14}$",
                        message = "mobileNumber must be E.164 format, e.g. +14155552671")
                String mobileNumber) {}
