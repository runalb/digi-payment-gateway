package com.runalb.ondemand_service.provider.dto;

import com.runalb.ondemand_service.user.dto.UserResponse;

public record ProviderDetailResponse(
        Long providerId,
        String bio,
        boolean isVerified,
        double averageRating,
        int profileCompletionPercentage,
        boolean isActive,
        String address,
        UserResponse user) {}
