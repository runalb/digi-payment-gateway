package com.digirestro.digi_payment_gateway.dto.merchantconfig;

public record MerchantConfigResponse(Long merchantId, String currency, String webhookUrl) {}
