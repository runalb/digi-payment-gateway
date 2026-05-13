package com.runalb.ondemand_service.business.dto;

public record BusinessPaymentChannelConfigUpdateRequest(
    Boolean isDeleted, 
    String configJson
) {}
