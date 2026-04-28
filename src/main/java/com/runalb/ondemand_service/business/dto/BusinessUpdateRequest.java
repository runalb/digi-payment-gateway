package com.runalb.ondemand_service.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record BusinessUpdateRequest(
        @Size(max = 255) String name,
        @Email @Size(max = 255) String email,
        Boolean isActive) {}
