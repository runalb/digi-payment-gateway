package com.runalb.ondemand_service.business.dto;

public record BusinessConfigResponse(
    Long businessId, 
    String currency, 
    String webhookUrl
) {}
