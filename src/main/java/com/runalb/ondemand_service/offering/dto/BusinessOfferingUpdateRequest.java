package com.runalb.ondemand_service.offering.dto;

import jakarta.validation.constraints.NotNull;

public record BusinessOfferingUpdateRequest(@NotNull Boolean isActive) {}
