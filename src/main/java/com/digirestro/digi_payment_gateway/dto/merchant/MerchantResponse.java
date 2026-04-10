package com.digirestro.digi_payment_gateway.dto.merchant;

public record MerchantResponse(Long id, String name, String email, String apiKey, Boolean isActive) {}
