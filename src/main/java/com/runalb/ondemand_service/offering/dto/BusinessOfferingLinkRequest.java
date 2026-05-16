package com.runalb.ondemand_service.offering.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BusinessOfferingLinkRequest(@NotEmpty List<@NotNull Long> catalogServiceIds) {}
