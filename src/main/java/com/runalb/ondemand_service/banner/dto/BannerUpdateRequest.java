package com.runalb.ondemand_service.banner.dto;

import jakarta.validation.constraints.Size;

public record BannerUpdateRequest(
        @Size(max = 255) String title,
        @Size(max = 2048) String imageUrl,
        Integer displayOrder) {}
