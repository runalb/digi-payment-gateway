package com.runalb.ondemand_service.booking.dto;

import com.runalb.ondemand_service.booking.enums.BookingStatusEnum;
import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.user.dto.UserResponse;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        BookingStatusEnum status,
        LocalDateTime scheduledAt,
        String notes,
        Long businessOfferingId,
        CatalogServiceResponse catalogService,
        UserResponse user,
        BusinessResponse business,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {}
