package com.runalb.ondemand_service.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BookingCreateRequest(
        @NotNull Long businessOfferingId, @NotNull LocalDateTime scheduledAt, String notes) {}
