package com.runalb.ondemand_service.booking.mapper;

import com.runalb.ondemand_service.booking.dto.BookingResponse;
import com.runalb.ondemand_service.booking.entity.BookingEntity;
import com.runalb.ondemand_service.business.mapper.BusinessDtoMapper;
import com.runalb.ondemand_service.catalog.mapper.CatalogDtoMapper;
import com.runalb.ondemand_service.user.mapper.UserDtoMapper;

public final class BookingDtoMapper {

    private BookingDtoMapper() {}

    public static BookingResponse toResponse(BookingEntity booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getScheduledAt(),
                booking.getNotes(),
                booking.getBusinessOffering().getId(),
                CatalogDtoMapper.toServiceResponse(booking.getCatalogService()),
                UserDtoMapper.toResponse(booking.getUser()),
                BusinessDtoMapper.toResponse(booking.getBusiness()),
                booking.getCreatedDateTime(),
                booking.getUpdatedDateTime());
    }
}
