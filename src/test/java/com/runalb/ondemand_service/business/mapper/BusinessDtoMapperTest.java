package com.runalb.ondemand_service.business.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.entity.BusinessEntity;
import org.junit.jupiter.api.Test;

class BusinessDtoMapperTest {

    @Test
    void toResponse_mapsAllFieldsAndDefaultsAverageRating() {
        BusinessEntity business = new BusinessEntity();
        business.setId(3L);
        business.setName("Acme Services");
        business.setEmail("acme@example.com");
        business.setIsDeleted(false);
        business.setBusinessType("SOLE_PROP");
        business.setDescription("Local provider");
        business.setIsVerified(true);
        business.setAverageRating(null);
        business.setAddress("123 Main St");
        business.setMobileNumber("+919876543210");

        BusinessResponse response = BusinessDtoMapper.toResponse(business);

        assertEquals(3L, response.id());
        assertEquals("Acme Services", response.name());
        assertEquals("acme@example.com", response.email());
        assertFalse(response.isDeleted());
        assertEquals("SOLE_PROP", response.businessType());
        assertEquals("Local provider", response.description());
        assertTrue(response.isVerified());
        assertEquals(0.0, response.averageRating());
        assertEquals("123 Main St", response.address());
        assertEquals("+919876543210", response.mobileNumber());
    }
}
