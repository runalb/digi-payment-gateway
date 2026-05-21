package com.runalb.ondemand_service.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.runalb.ondemand_service.booking.dto.BookingResponse;
import com.runalb.ondemand_service.booking.entity.BookingEntity;
import com.runalb.ondemand_service.booking.enums.BookingStatusEnum;
import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.offering.entity.BusinessOfferingEntity;
import com.runalb.ondemand_service.user.entity.UserEntity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BookingDtoMapperTest {

    @Test
    void toResponse_mapsBookingWithNestedRecords() {
        LocalDateTime scheduledAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 20, 9, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 5, 20, 9, 30);

        CatalogCategoryEntity category = new CatalogCategoryEntity();
        category.setId(1L);
        category.setName("Cleaning");
        category.setDisplayOrder(0);

        CatalogServiceEntity catalogService = new CatalogServiceEntity();
        catalogService.setId(2L);
        catalogService.setName("Deep clean");
        catalogService.setDisplayOrder(0);
        catalogService.setCatalogCategory(category);

        BusinessEntity business = new BusinessEntity();
        business.setId(3L);
        business.setName("Acme");
        business.setEmail("acme@example.com");
        business.setIsDeleted(false);
        business.setIsVerified(true);

        BusinessOfferingEntity offering = new BusinessOfferingEntity();
        offering.setId(4L);
        offering.setBusiness(business);
        offering.setCatalogService(catalogService);

        UserEntity user = new UserEntity();
        user.setId(5L);
        user.setEmail("user@example.com");
        user.setName("Customer");
        user.setIsVerified(true);

        BookingEntity booking = new BookingEntity();
        booking.setId(6L);
        booking.setStatus(BookingStatusEnum.PENDING);
        booking.setScheduledAt(scheduledAt);
        booking.setNotes("Please call on arrival");
        booking.setUser(user);
        booking.setBusiness(business);
        booking.setBusinessOffering(offering);
        booking.setCatalogService(catalogService);
        booking.setCreatedDateTime(createdAt);
        booking.setUpdatedDateTime(updatedAt);

        BookingResponse response = BookingDtoMapper.toResponse(booking);

        assertEquals(6L, response.id());
        assertEquals(BookingStatusEnum.PENDING, response.status());
        assertEquals(scheduledAt, response.scheduledAt());
        assertEquals("Please call on arrival", response.notes());
        assertEquals(4L, response.businessOfferingId());
        assertEquals(createdAt, response.createdDateTime());
        assertEquals(updatedAt, response.updatedDateTime());
        assertEquals(2L, response.catalogService().id());
        assertEquals("Deep clean", response.catalogService().name());
        assertEquals(1L, response.catalogService().category().id());
        assertEquals(5L, response.user().id());
        assertEquals("user@example.com", response.user().email());
        assertEquals(3L, response.business().id());
        assertEquals("Acme", response.business().name());
    }
}
