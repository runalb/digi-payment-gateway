package com.runalb.ondemand_service.banner.mapper;

import com.runalb.ondemand_service.banner.dto.BannerResponse;
import com.runalb.ondemand_service.banner.entity.BannerEntity;

public final class BannerDtoMapper {

    private BannerDtoMapper() {}

    public static BannerResponse toResponse(BannerEntity banner) {
        return new BannerResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getImageUrl(),
                banner.getDisplayOrder());
    }
}
