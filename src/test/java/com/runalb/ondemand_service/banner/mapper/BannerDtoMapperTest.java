package com.runalb.ondemand_service.banner.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.runalb.ondemand_service.banner.dto.BannerResponse;
import com.runalb.ondemand_service.banner.entity.BannerEntity;
import org.junit.jupiter.api.Test;

class BannerDtoMapperTest {

    @Test
    void toResponse_mapsAllFields() {
        BannerEntity banner = new BannerEntity();
        banner.setId(1L);
        banner.setTitle("Summer promo");
        banner.setImageUrl("https://cdn.example.com/banner.jpg");
        banner.setDisplayOrder(2);

        BannerResponse response = BannerDtoMapper.toResponse(banner);

        assertEquals(1L, response.id());
        assertEquals("Summer promo", response.title());
        assertEquals("https://cdn.example.com/banner.jpg", response.imageUrl());
        assertEquals(2, response.displayOrder());
    }
}
