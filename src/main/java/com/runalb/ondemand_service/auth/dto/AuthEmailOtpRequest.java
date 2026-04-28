package com.runalb.ondemand_service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthEmailOtpRequest(
        @NotBlank @Email @Size(max = 255) String email) {}
