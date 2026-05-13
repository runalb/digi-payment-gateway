package com.runalb.ondemand_service.business.dto;

public record BusinessResponse(
    Long id, 
    String name, 
    String email, 
    String apiKey, 
    Boolean isDeleted
) {}
