package com.digirestro.digi_payment_gateway.portal.user.dto;

public record UserResponse(
        Long id,
        String email,
        String mobileNumber,
        String name,
        Boolean isActive,
        Boolean isVerified) {}
