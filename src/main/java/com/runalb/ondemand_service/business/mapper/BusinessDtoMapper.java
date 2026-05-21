package com.runalb.ondemand_service.business.mapper;

import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.entity.BusinessEntity;

public final class BusinessDtoMapper {

    private BusinessDtoMapper() {}

    public static BusinessResponse toResponse(BusinessEntity business) {
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getEmail(),
                business.getIsDeleted(),
                business.getBusinessType(),
                business.getDescription(),
                Boolean.TRUE.equals(business.getIsVerified()),
                business.getAverageRating() != null ? business.getAverageRating() : 0.0,
                business.getAddress(),
                business.getMobileNumber());
    }
}
