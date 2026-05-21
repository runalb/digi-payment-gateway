package com.runalb.ondemand_service.banner.dto;

public record BannerResponse(
        Long id, String title, String imageUrl, Integer displayOrder) {}
