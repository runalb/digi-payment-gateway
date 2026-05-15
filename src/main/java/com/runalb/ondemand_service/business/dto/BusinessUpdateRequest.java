package com.runalb.ondemand_service.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Null fields are left unchanged. */
public record BusinessUpdateRequest(
        @Size(max = 255) String name,
        @Email @Size(max = 255) String email,
        @Size(max = 255) String businessType,
        @Size(max = 8000) String description,
        @Size(max = 2000) String address,
        @Pattern(
                        regexp = "^$|^\\+?[1-9]\\d{7,14}$",
                        message = "mobileNumber must be E.164 format, e.g. +14155552671")
                @Size(max = 20)
                String mobileNumber) {}
