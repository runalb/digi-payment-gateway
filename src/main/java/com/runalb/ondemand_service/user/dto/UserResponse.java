package com.runalb.ondemand_service.user.dto;

public record UserResponse(
        Long id,
        String email,
        String mobileNumber,
        String name,
        Boolean isActive,
        Boolean isVerified) {}
