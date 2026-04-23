package com.digirestro.digi_payment_gateway.user.dto;

public record UserResponse(
        Long id,
        String email,
        String mobileNumber,
        String name,
        Boolean isActive,
        Boolean isVerified) {}
