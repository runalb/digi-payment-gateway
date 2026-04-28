package com.runalb.ondemand_service.merchant.dto;

public record MerchantConfigResponse(Long merchantId, String currency, String webhookUrl) {}
