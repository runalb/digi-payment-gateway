package com.runalb.ondemand_service.merchant.dto;

public record MerchantResponse(Long id, String name, String email, String apiKey, Boolean isActive) {}
