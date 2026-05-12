package com.runalb.ondemand_service.provider.dto;

import jakarta.validation.constraints.Size;

public record ProviderCreateRequest(
        @Size(max = 8000) String bio,
        @Size(max = 2000) String address) {}
