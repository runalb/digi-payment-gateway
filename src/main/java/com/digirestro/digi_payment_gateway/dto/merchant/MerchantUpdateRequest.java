package com.digirestro.digi_payment_gateway.dto.merchant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record MerchantUpdateRequest(
        @Size(max = 255) String name,
        @Email @Size(max = 255) String email,
        Boolean isActive) {}
