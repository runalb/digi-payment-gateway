package com.digirestro.digi_payment_gateway.portal.merchant.dto;

public record MerchantConfigResponse(Long merchantId, String currency, String webhookUrl) {}
