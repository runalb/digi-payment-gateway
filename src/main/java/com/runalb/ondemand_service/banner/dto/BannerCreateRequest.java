package com.runalb.ondemand_service.banner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BannerCreateRequest(
        @Size(max = 255) String title,
        @NotBlank @Size(max = 2048) String imageUrl,
        Integer displayOrder) {}
