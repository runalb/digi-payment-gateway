package com.runalb.ondemand_service.provider.dto;

import jakarta.validation.constraints.Size;

/** Null fields are left unchanged. */
public record ProviderUpdateRequest(
        @Size(max = 8000) String bio,
        @Size(max = 2000) String address,
        Boolean isActive) {}
