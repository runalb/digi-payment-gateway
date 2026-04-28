package com.runalb.ondemand_service.user.dto;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String mobileNumber,
        String name,
        Boolean isActive,
        Boolean isVerified,
        List<String> roles) {}
