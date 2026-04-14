package com.digirestro.digi_payment_gateway.portal.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Email @Size(max = 255) String email,
        @Size(max = 255) String name,
        @Pattern(
                        regexp = "^$|^\\+?[1-9]\\d{7,14}$",
                        message = "mobileNumber must be E.164 format, e.g. +14155552671")
                String mobileNumber,
        @Size(min = 8, max = 128) String password,
        Boolean isVerified) {}
